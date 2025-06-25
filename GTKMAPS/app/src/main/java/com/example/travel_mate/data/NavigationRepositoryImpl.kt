package com.example.travel_mate.data

import android.util.Log
import com.example.travel_mate.domain.LocationRepository
import com.example.travel_mate.domain.NavigationRepository
import com.example.travel_mate.domain.RouteNodeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import org.osmdroid.views.overlay.Polyline
import kotlin.math.abs
import kotlin.math.sqrt

class NavigationRepositoryImpl: NavigationRepository {

    private val routeNodeRepository: RouteNodeRepository by inject(RouteNodeRepository::class.java)
    private val locationRepository: LocationRepository by inject(LocationRepository::class.java)

    private val routeUtilityClass = RouteUtilityClass()

    private val _navigationState = MutableStateFlow(NavigationState())
    override val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    private val _navigationInfoState = MutableStateFlow(NavigationInfoState())
    override val navigationInfoState: StateFlow<NavigationInfoState> = _navigationInfoState.asStateFlow()

    private val navigationCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val navigationComputingDispatcher: CoroutineDispatcher = Dispatchers.Default

    private val navigationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var navigationJob: Job? = null
    private var extrapolationJob: Job? = null

    private val navigationProximityDistance = 0.040
    private val duration = 3000L
    private var extrapolationDuration: Long = 50 //ms
    private var lastKnownLocation: Coordinates = Coordinates()

    private var extrapolationStart: Coordinates = lastKnownLocation
    private var extrapolationEnd: Coordinates = lastKnownLocation
    private var extrapolationStartTime: Long = 50

    override fun getCurrentNodeIndex() = _navigationState.value.currentNavigationRouteNodeIndex

    override fun setEndOfNavigation(isEnd: Boolean) {

        _navigationInfoState.update {

            it.copy(
                endOfNavigation = isEnd
            )
        }
    }

    override fun navigateToPlaceInRoute(destination: RouteNode, transportMode: String) {

        navigationScope.launch {

            val goalLocationNode = RouteNode(
                coordinate = destination.coordinate
            )

            _navigationState.update {
                it.copy(
                    currentNavigationRouteNodeIndex = 1,
                    navigationMode = transportMode,
                    navigationGoal = goalLocationNode
                )
            }

            _navigationInfoState.update {

                it.copy(
                    startedFrom = 0,
                    isStarted = true
                )
            }

            startNavigationJobs(
                goalLocationNode = goalLocationNode
            )
        }
    }

    override fun navigateToNextPlaceInRoute(nextRouteNode: RouteNode) {

        navigationScope.launch {

            //Todo create a usesCase for navigation to next place in route and
            // get the next destination there

                _navigationState.update {
                    it.copy(
                        currentNavigationRouteNodeIndex = it.currentNavigationRouteNodeIndex + 1,
                        navigationGoal = nextRouteNode
                    )
                }

                _navigationInfoState.update {

                    it.copy(
                        startedFrom = 1,
                        endOfRoute = false,
                        isStarted = true
                    )
                }

                startNavigationJobs(
                    goalLocationNode = nextRouteNode
                )
        }
    }

    override fun navigateToCustomPlace(coordinates: Coordinates, transportMode: String) {

        navigationScope.launch {

            val goalLocationNode = RouteNode(
                coordinate = coordinates
            )

            _navigationState.update {
                it.copy(
                    navigationMode = transportMode,
                    navigationGoal = goalLocationNode
                )
            }
            _navigationInfoState.update {

                it.copy(
                    startedFrom = 1,
                    isStarted = true
                )
            }

            startNavigationJobs(
                goalLocationNode = goalLocationNode
            )
        }
    }

    fun startNavigationJob(goalLocationNode: RouteNode) {

        if (navigationJob?.isActive == true) return

        navigationJob = navigationScope.launch {

            locationRepository.startLocationUpdates()

            //The location of the user at the start of the navigation
            var initialLocation: Coordinates? = null

            do {
                initialLocation = updateCurrentLocation()
            } while (initialLocation == null)

            //create a node representing the initial location of the user
            //this is the start point of the navigation
            val currentLocationNode = RouteNode(
                coordinate = initialLocation
            )

            //find the route between the goal of the navigation and the start point
            val navigationRouteNode = routeNodeRepository.getRouteNode(
                stop1 = currentLocationNode.coordinate!!,
                stop2 = goalLocationNode.coordinate!!
            )
            //select the appropriate route based on the selected transport mode
            var currentRoute = when (_navigationState.value.navigationMode) {
                "driving-car" -> navigationRouteNode.carRouteSteps
                else -> navigationRouteNode.walkRouteSteps
            }
            var currentRoutePolyLine = when (_navigationState.value.navigationMode) {
                "driving-car" -> navigationRouteNode.carPolyLine
                else -> navigationRouteNode.walkPolyLine
            }

            lastKnownLocation = initialLocation

            /*
            update the last known location of the user
            if the initial location is null then use the coordinates of the start point
            TODO That is actually the same that is an issue
            */
            onNewMatchedLocation(initialLocation)

            //initial increase count, last distance ant the target segment index
            var closestIndex = 0
            var prevKnownLocation = initialLocation
            var distanceIncreaseCount = 0
            var lastDistance = Double.MAX_VALUE
            var targetSegmentIndex = 1

            //update the UI with the initial values
            _navigationInfoState.update {

                it.copy(
                    endOfRoute = false,
                    currentRouteStep = currentRoute[0]
                )
            }
            _navigationState.update {

                it.copy(
                    routeSteps = currentRoute,
                    routePolyLines = currentRoutePolyLine,
                )
            }

            //in every 'duration' milliseconds (currently 1500)
            while (isActive) {

                //get the users current location
                val userLocation = updateCurrentLocation() ?: continue

                Log.d("updateCurrentLocation", userLocation.getLatitude().toString() + " " + userLocation.getLongitude().toString())

                val closestSteps = findClosestSegmentToSnapTo(
                    pointToSnap = userLocation,
                    segments = currentRoute,
                    closestIndex
                )

                closestIndex = currentRoute.indexOf(closestSteps.first)

                //initialize the projected position
                var projectedPosition = matchLocationToSegment(
                    segmentC1 = closestSteps.first.coordinates,
                    segmentC2 = closestSteps.second.coordinates,
                    location = userLocation
                )

                //determine the distance between the last known location and the
                // target segment
                val currentDistance = routeUtilityClass.haversine(
                    startLat = projectedPosition.getLatitude(),
                    startLon = projectedPosition.getLongitude(),
                    endLat = currentRoute[targetSegmentIndex].coordinates.getLatitude(),
                    endLon = currentRoute[targetSegmentIndex].coordinates.getLongitude()
                )

                //update the interpolation loop
                onNewMatchedLocation(projectedPosition)

                //the lastKnownLocation and the projectedPosition is used to figure out the
                // (approximate*) passed distance in 'duration' time
                // (*approximate due to the nature of gps locations)
                val proximityDistance = calculateProximityDistance(
                    coordinates0 = prevKnownLocation!!,
                    coordinates1 = projectedPosition
                )

                prevKnownLocation = projectedPosition
                // Check for proximity
                //if the distance is less than 'proximityDistance' meters
                if (currentDistance < proximityDistance) {

                    //if the target segment is not the last one
                    if ( targetSegmentIndex < currentRoute.size - 2 ) {

                        // Move to next segment
                        targetSegmentIndex = findNextValidTargetSegment(
                            currentSegmentIndex = targetSegmentIndex,
                            proximityDistance = proximityDistance,
                            steps = currentRoute
                        )

                        if (currentRoute[targetSegmentIndex].instruction != null) {

                            //update the StateFlow with the found instruction
                            //update the previous instruction with the current
                            //and update the current with the newly found one
                            _navigationInfoState.update {

                                it.copy(
                                    prevRouteStep = it.currentRouteStep,
                                    currentRouteStep = currentRoute[targetSegmentIndex]
                                )
                            }
                        }

                        lastDistance = Double.MAX_VALUE // reset
                        distanceIncreaseCount = 0

                        Log.d("restartNavigation", "count reset")
                    } else if (targetSegmentIndex == currentRoute.size - 1 ) {

                        //todo include this check in the use case for navigation

                        _navigationInfoState.update {

                            it.copy(
                                endOfRoute = true
                            )
                        }

                        stopNavigationJobs(
                            removeData = false
                        )
                    }
                    //if the distance is greater than 'proximityDistance' meters
                } else {

                    //determine the actual distance between the user and the target segment
                    val actualDistance = routeUtilityClass.haversine(
                        startLat = userLocation.getLatitude(),
                        startLon = userLocation.getLongitude(),
                        endLat = currentRoute[targetSegmentIndex].coordinates.getLatitude(),
                        endLon = currentRoute[targetSegmentIndex].coordinates.getLongitude()
                    )

                    var avgDistance = ((actualDistance) + (currentDistance)) / 2

                    // Detect wrong direction
                    // if the actual distance is greater than the last distance
                    // or the distance between the matched point and the target segment is the same
                    // as before
                    // that means the user goes in a completely wrong direction
                    // thus increment the wrong direction counter
                    if (avgDistance > lastDistance ) {

                        distanceIncreaseCount++
                    } else {

                        distanceIncreaseCount = 0
                        Log.d("restartNavigation", "count reset from else branch")
                    }
                    // set the last distance's value as the current distance
                    lastDistance = avgDistance

                    //if the counter is at least 4
                    if (distanceIncreaseCount >= 4) {

                        restartNavigation(
                            goalLocationNode = goalLocationNode,
                            removeData = true
                        )
                        Log.d("restartNavigation", "navigation restarted")

                    }
                }
                delay(duration)
            }
        }

    }

    private suspend fun findNextValidTargetSegment(currentSegmentIndex: Int, proximityDistance: Double, steps: List<RouteStep>): Int {

        return withContext(navigationComputingDispatcher) {

            var passedDistance = routeUtilityClass.haversine(
                startLat = steps[currentSegmentIndex].coordinates.getLatitude(),
                startLon = steps[currentSegmentIndex].coordinates.getLongitude(),
                endLat = steps[currentSegmentIndex+1].coordinates.getLatitude(),
                endLon = steps[currentSegmentIndex+1].coordinates.getLongitude()
            )
            var index = currentSegmentIndex + 1

            while (passedDistance < proximityDistance && steps[index].instruction == null) {

                passedDistance+= routeUtilityClass.haversine(
                    startLat = steps[index].coordinates.getLatitude(),
                    startLon = steps[index].coordinates.getLongitude(),
                    endLat = steps[index+1].coordinates.getLatitude(),
                    endLon = steps[index+1].coordinates.getLongitude()
                )
                index++
            }

            return@withContext index
        }
    }

    override fun stopNavigationJob(removeData: Boolean) {

        navigationJob?.cancel()

        locationRepository.stopLocationUpdates()

        if (removeData) {

            _navigationState.update {

                it.copy(
                    routePolyLines = null,
                    navigationGoal = null,
                    currentLocation = null
                )
            }
            _navigationInfoState.update {

                it.copy(
                    isStarted = false,
                    prevRouteStep = null,
                    currentRouteStep = null,
                )
            }
        }
    }

    override fun restartNavigation(goalLocationNode: RouteNode, removeData: Boolean) {

        stopNavigationJobs(removeData = removeData)

        startNavigationJobs(
            goalLocationNode = goalLocationNode
        )

    }

    override fun stopNavigationJobs(removeData: Boolean) {

        stopNavigationJob(removeData).also {
            stopExtrapolationLoop()
        }
    }

    private fun startNavigationJobs(goalLocationNode: RouteNode) {

        startNavigationJob(
            goalLocationNode = goalLocationNode
        ).also {
            startExtrapolationLoop()
        }

    }

    fun onNewMatchedLocation(newLocation: Coordinates) {
        // Just update the interpolation values; coroutine will pick it up

        extrapolationStart = lastKnownLocation
        extrapolationEnd = newLocation

        extrapolationStartTime = 50

        lastKnownLocation = newLocation
    }

    suspend fun calculateProximityDistance(coordinates0: Coordinates, coordinates1: Coordinates): Double {

        return withContext(navigationComputingDispatcher) {

            val distance = routeUtilityClass.haversine(
                startLat = coordinates0.getLatitude(),
                startLon = coordinates0.getLongitude(),
                endLat = coordinates1.getLatitude(),
                endLon = coordinates1.getLongitude()
            )

            return@withContext when(distance < navigationProximityDistance) {
                true -> navigationProximityDistance
                else -> distance * 2
            }
        }
    }

    fun startExtrapolationLoop() {
        if (extrapolationJob?.isActive == true) return

        extrapolationJob = navigationScope.launch {

            while (isActive) {

                val secondsAhead = extrapolationStartTime / 1000.0

                val moved = moveAlongSegment(
                    from = extrapolationStart,
                    to = extrapolationEnd,
                    secondsAhead = secondsAhead
                )

                _navigationState.update {

                    it.copy(
                        currentLocation = moved
                    )
                }

                delay(extrapolationDuration)

                extrapolationStartTime = extrapolationStartTime + extrapolationDuration
            }
        }
    }

    override fun stopExtrapolationLoop() {
        extrapolationJob?.cancel()
    }

    private suspend fun moveAlongSegment(
        from: Coordinates,
        to: Coordinates,
        secondsAhead: Double
    ): Coordinates {

        return withContext(navigationComputingDispatcher) {

            val dx = to.getLongitude() - from.getLongitude()
            val dy = to.getLatitude() - from.getLatitude()

            val fraction = (secondsAhead / (duration / 1000)).coerceIn(0.0, 1.0)

            val newLon = from.getLongitude() + fraction * dx
            val newLat = from.getLatitude() + fraction * dy

            return@withContext Coordinates(
                latitude = newLat,
                longitude = newLon
            )
        }
    }

    private suspend fun matchLocationToSegment(segmentC1: Coordinates, segmentC2: Coordinates, location: Coordinates): Coordinates {

        return withContext(navigationComputingDispatcher) {

            val ax = segmentC1.getLongitude()
            val ay = segmentC1.getLatitude()
            val bx = segmentC2.getLongitude()
            val by = segmentC2.getLatitude()
            val px = location.getLongitude()
            val py = location.getLatitude()

            val dx = bx - ax
            val dy = by - ay

            if (dx == 0.0 && dy == 0.0) return@withContext segmentC1 // a == b

            val t = ((px - ax) * dx + (py - ay) * dy) / (dx * dx + dy * dy)
            val clampedT = t.coerceIn(0.0, 1.0) // ensures projection is on the segment

            val closestX = ax + clampedT * dx
            val closestY = ay + clampedT * dy

            return@withContext Coordinates(closestY, closestX)
        }
    }

    /** [findClosestSegmentToSnapTo]
     *  find that part of the route (the route Segment) that is the closest to the given point
     *  to which a point should be snapped to during navigation
     *
     *  @param [Coordinates] of a point that needs to be snapped to a route segment
     *  @param [List] of [RouteStep]s from the segment is to be selected
     *  @param [previousIndex] the index of the previous closest [RouteStep]
     *
     *  @return a [Pair] of the two endpoints of the found segment
     */
    private suspend fun findClosestSegmentToSnapTo(
        pointToSnap: Coordinates,
        segments: List<RouteStep>,
        previousIndex: Int
    ) : Pair<RouteStep, RouteStep> {

        return withContext(navigationComputingDispatcher) {
            val reduced = reduceSegmentsToXMostPossible(
                segments = segments,
                closeToIndex = previousIndex,
                threshold = 4
            )

            val closest = findClosestRouteStepToPoint(
                segments = reduced,
                point = pointToSnap
            )

            val indexOfClosest = segments.indexOf(closest)

            val distToIndexMinusOne = if (indexOfClosest > 0) findDistanceBetweenPointAndLine(
                startOfLine = closest.coordinates,
                endOfLine = segments[indexOfClosest - 1].coordinates,
                point = pointToSnap
            ) else Double.MAX_VALUE

            val distToIndexPlusOne = if (indexOfClosest == segments.size - 1) Double.MAX_VALUE
            else findDistanceBetweenPointAndLine(
                startOfLine = closest.coordinates,
                endOfLine = segments[indexOfClosest + 1].coordinates,
                point = pointToSnap
            )

            return@withContext when (distToIndexMinusOne > distToIndexPlusOne) {
                true -> {
                    Pair(closest, segments[indexOfClosest + 1])
                }

                false -> {
                    Pair(closest, segments[indexOfClosest - 1])
                }
            }
        }
    }

    /** [reduceSegmentsToXMostPossible]
     *  amateur solution to reduce the given [RouteStep]s to the [threshold]*2 most possible
     *  to avoid checking the whole list of segments when searching for the closest one during navigation
     *
     *  @param [segments] the whole list of [RouteStep]s
     *  @param [closeToIndex] the index of the previous closest [RouteStep]
     *  @param [threshold] the number of returned [RouteStep]s after and before the [closeToIndex]
     *
     *  @return the reduced list
     */
    private suspend fun reduceSegmentsToXMostPossible(segments: List<RouteStep>, closeToIndex: Int, threshold: Int): List<RouteStep> {

        return withContext(navigationComputingDispatcher) {
            val reduced: MutableList<RouteStep> = mutableListOf()

            if (closeToIndex - threshold < 0) {

                for (i in 0 until closeToIndex) {
                    reduced.add(segments[i])
                }
            } else {

                for (i in closeToIndex - threshold until closeToIndex) {
                    reduced.add(segments[i])
                }
            }

            if (closeToIndex + threshold > segments.size - 1) {

                for (i in closeToIndex until segments.size - 1) {
                    reduced.add(segments[i])
                }
            } else {

                for (i in closeToIndex until closeToIndex + threshold) {
                    reduced.add(segments[i])
                }
            }

            return@withContext reduced
        }
    }

    /** [findClosestRouteStepToPoint]
     *  find the closest [RouteStep] to the given [Coordinates]
     *
     *  @param [List] of [RouteStep]s
     *  @param [Coordinates] of the point of which closest [RouteStep] is needed
     *
     *  @return the closest [RouteStep]
     */
    private suspend fun findClosestRouteStepToPoint(segments: List<RouteStep>, point: Coordinates): RouteStep {

        return withContext(navigationComputingDispatcher) {

            return@withContext segments.minBy {
                routeUtilityClass.haversine(
                    startLat = it.coordinates.getLatitude(),
                    startLon = it.coordinates.getLongitude(),
                    endLat = point.getLatitude(),
                    endLon = point.getLongitude()
                )
            }
        }
    }

    /** [findDistanceBetweenPointAndLine]
     *  calculate the distance of a point and a line defined by 2 other points
     *
     *  @param [Coordinates] of the start of the line
     *  @param [Coordinates] of the end of the line
     *  @param [Coordinates] of the point
     *
     *  @return the distance between the line and the point
     */
    private suspend fun findDistanceBetweenPointAndLine(
        startOfLine: Coordinates,
        endOfLine: Coordinates,
        point: Coordinates
    ): Double {

        return withContext(navigationComputingDispatcher) {

            val dLat = endOfLine.getLatitude() - startOfLine.getLatitude()
            val dLon = endOfLine.getLongitude() - startOfLine.getLongitude()

            val upperPart = abs(
                (dLat * point.getLongitude()) - (dLon * point.getLatitude())
                        + (endOfLine.getLongitude() * startOfLine.getLatitude())
                        - (endOfLine.getLatitude() * startOfLine.getLongitude())
            )
            val lowerPart = sqrt((dLat * dLat) + (dLon * dLon))

            return@withContext upperPart / lowerPart
        }
    }

    fun findNextInstruction(currentRouteStepIndex: Int, routeSteps: List<RouteStep>): Int {

        val nextInstruction = routeSteps.first { routeSteps.indexOf(it) > currentRouteStepIndex && it.instruction != null }

        return routeSteps.indexOf(nextInstruction)
    }

    override suspend fun updateCurrentLocation(): Coordinates?{

        return withContext(navigationCoroutineDispatcher) {

            val location = locationRepository.updateCurrentLocation()

            if (location != null) {
                return@withContext Coordinates(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            }
            return@withContext null
        }
    }

    data class NavigationState(
        val currentNavigationRouteNodeIndex: Int = 0,
        val navigationMode: String? = null,
        val navigationGoal: RouteNode? = null,
        val routeSteps: List<RouteStep>? = null,
        val routePolyLines: Polyline? = null,
        val currentLocation: Coordinates? = null,
    )
    data class NavigationInfoState(
        val isStarted: Boolean = false,
        val startedFrom: Int = 0, // 0 -> Route, 1 -> CustomPlace
        val endOfRoute: Boolean = false,
        val endOfNavigation: Boolean = false,
        val prevRouteStep: RouteStep? = null,
        val currentRouteStep: RouteStep? = null
    )
}
package com.example.features.navigation.data.repositories

import android.util.Log
import com.example.app.location.domain.repository.LocationRepository
import com.example.features.navigation.domain.mappers.toFlowOfCurrentLocationDomainModel
import com.example.features.navigation.domain.mappers.toFlowOfNavigationMapDataDomainModel
import com.example.features.navigation.domain.mappers.toFlowOfNavigationInfoDomainModel
import com.example.features.navigation.domain.models.CoordinatesNavigationDomainModel
import com.example.features.navigation.domain.models.CurrentLocationNavigationDomainModel
import com.example.features.navigation.domain.models.CurrentLocationStateNavigationDomainModel
import com.example.features.navigation.domain.models.NavigationMapDataNavigationDomainModel
import com.example.features.navigation.domain.models.NavigationNavigationDomainModel
import com.example.features.navigation.domain.models.NavigationInfoNavigationDomainModel
import com.example.features.navigation.domain.models.NavigationParametersNavigationDomainModel
import com.example.features.navigation.domain.models.NavigationStateNavigationDomainModel
import com.example.features.navigation.domain.models.RouteNodeNavigationDomainModel
import com.example.features.navigation.domain.models.RouteStepNavigationDomainModel
import com.example.features.navigation.domain.repositories.NavigationRepository
import com.example.features.navigation.domain.repositories.RouteNodeRepository
import com.example.core.utils.RouteUtilityClass
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import kotlin.collections.minBy
import kotlin.math.abs
import kotlin.math.sqrt

class NavigationRepositoryImpl: NavigationRepository {

    private val routeNodeRepository: RouteNodeRepository by inject(RouteNodeRepository::class.java)
    private val locationRepository: LocationRepository by inject(LocationRepository::class.java)

    private val routeUtilityClass = RouteUtilityClass()

    private val _navigationState = MutableStateFlow(NavigationStateNavigationDomainModel())

    private val currentLocationState = MutableStateFlow(CurrentLocationStateNavigationDomainModel())

    private val navigationCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val navigationComputingDispatcher: CoroutineDispatcher = Dispatchers.Default

    private val navigationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var navigationJob: Job? = null
    private var extrapolationJob: Job? = null

    private val navigationProximityDistance = 0.040
    private val duration = 3000L
    private var extrapolationDuration: Long = 50 //ms

    private val wrongDirectionMax = 4
    private var lastKnownLocation: CoordinatesNavigationDomainModel =
        CoordinatesNavigationDomainModel()

    private var extrapolationStart: CoordinatesNavigationDomainModel = lastKnownLocation
    private var extrapolationEnd: CoordinatesNavigationDomainModel = lastKnownLocation
    private var extrapolationStartTime: Long = 50

    override fun getNavigationInfo(): Flow<NavigationInfoNavigationDomainModel> {
        return _navigationState.toFlowOfNavigationInfoDomainModel()
    }

    override fun getNavigationMapData(): Flow<NavigationMapDataNavigationDomainModel> {
        return _navigationState.toFlowOfNavigationMapDataDomainModel()
    }

    override fun getNavigationCurrentLocation(): Flow<CurrentLocationNavigationDomainModel> {
        return currentLocationState.toFlowOfCurrentLocationDomainModel()
    }

    override fun getCurrentNodeIndex(): Int {

        return _navigationState.value.parameters.currentNavigationRouteNodeIndex
    }

    override fun initNavigation(navigationMode: String, destinationCoordinates: List<CoordinatesNavigationDomainModel>) {

        _navigationState.update {
            it.copy(
                parameters = NavigationParametersNavigationDomainModel(
                    destinationCoordinates = destinationCoordinates,
                    currentNavigationRouteNodeIndex = 0,
                    navigationMode = navigationMode
                )
            )
        }
    }

    override fun resetNavigation() {

        _navigationState.update {

            it.copy(
                navigation = NavigationNavigationDomainModel.Default
            )
        }
    }

    override fun navigateToPlaceInRoute() {

        navigationScope.launch {



            val nextRouteNodeCoordinates = _navigationState.value.parameters.destinationCoordinates[_navigationState.value.parameters.currentNavigationRouteNodeIndex]

            startNavigationJobs(
                goalCoordinates = nextRouteNodeCoordinates
            )
        }
    }

    fun startNavigationJob(goalCoordinates: CoordinatesNavigationDomainModel /*goalLocationNode: RouteNodeNavigationDomainModel*/) {

        if (navigationJob?.isActive == true) return

        navigationJob = navigationScope.launch {

            locationRepository.startLocationUpdates()

            //The location of the user at the start of the navigation
            var initialLocation: CoordinatesNavigationDomainModel?

            do {
                initialLocation = updateCurrentLocation()
            } while (initialLocation == null)

            //create a node representing the initial location of the user
            //this is the start point of the navigation
            /*val currentLocationNode = RouteNodeNavigationDomainModel(
                coordinate = initialLocation
            )*/

            //find the route between the goal of the navigation and the start point
            var navigationRouteNode: RouteNodeNavigationDomainModel?

            do {
                navigationRouteNode = routeNodeRepository.getRouteNode(
                    stop1 = initialLocation,
                    stop2 = goalCoordinates
                )
            } while (navigationRouteNode == null)

            //select the appropriate route based on the selected transport mode
            val currentRoute = when (_navigationState.value.parameters.navigationMode) {
                "driving-car" -> navigationRouteNode.carRouteSteps
                else -> navigationRouteNode.walkRouteSteps
            }
            val currentRoutePolyLine = when (_navigationState.value.parameters.navigationMode) {
                "driving-car" -> navigationRouteNode.carPolyLine
                else -> navigationRouteNode.walkPolyLine
            }

            lastKnownLocation = initialLocation
            onNewMatchedLocation(initialLocation)

            //initial increase count, last distance ant the target segment index
            var closestIndex = 0
            var prevKnownLocation = initialLocation
            var distanceIncreaseCount = 0
            var lastDistance = Double.MAX_VALUE
            var targetSegmentIndex = 1

            _navigationState.update {

                it.copy(
                    navigation = NavigationNavigationDomainModel.Navigation(
                        goal = goalCoordinates,
                        routeSteps = currentRoute,
                        routePolyLines = currentRoutePolyLine,
                        prevRouteStep = null,
                        currentRouteStep = currentRoute[0]
                    )
                )
            }

            //in every 'duration' milliseconds
            while (isActive) {

                //get the users current location
                val userLocation = updateCurrentLocation() ?: continue

                Log.d("updateCurrentLocation", userLocation.latitude.toString() + " " + userLocation.longitude.toString())

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
                    startLat = projectedPosition.latitude,
                    startLon = projectedPosition.longitude,
                    endLat = currentRoute[targetSegmentIndex].coordinates.latitude,
                    endLon = currentRoute[targetSegmentIndex].coordinates.longitude
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
                            _navigationState.update {

                                it.copy(
                                    navigation = (it.navigation as NavigationNavigationDomainModel.Navigation).copy(
                                        prevRouteStep = (it.navigation as NavigationNavigationDomainModel.Navigation).currentRouteStep,
                                        currentRouteStep = currentRoute[targetSegmentIndex]
                                    )
                                )
                            }
                        }

                        lastDistance = Double.MAX_VALUE // reset
                        distanceIncreaseCount = 0

                        Log.d("restartNavigation", "count reset")
                    } else if (targetSegmentIndex == currentRoute.size - 1 ) {

                        delay(duration*2)

                        val hasNext = _navigationState.value.parameters.currentNavigationRouteNodeIndex < _navigationState.value.parameters.destinationCoordinates.size - 1

                        stopNavigationJobs()

                        _navigationState.update {

                            it.copy(
                                parameters = it.parameters.copy(
                                    currentNavigationRouteNodeIndex = it.parameters.currentNavigationRouteNodeIndex + 1
                                ),
                                navigation = NavigationNavigationDomainModel.Arrived(
                                    hasNextDestination = hasNext,
                                    finalRouteStep = (_navigationState.value.navigation as NavigationNavigationDomainModel.Navigation).currentRouteStep
                                )
                            )
                        }
                    }
                    //if the distance is greater than 'proximityDistance' meters
                } else {

                    //determine the actual distance between the user and the target segment
                    val actualDistance = routeUtilityClass.haversine(
                        startLat = userLocation.latitude,
                        startLon = userLocation.longitude,
                        endLat = currentRoute[targetSegmentIndex].coordinates.latitude,
                        endLon = currentRoute[targetSegmentIndex].coordinates.longitude
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

                    //if the counter is at least 'wrongDirectionMax'
                    if (distanceIncreaseCount >= wrongDirectionMax) {

                        restartNavigation(
                            goalCoordinates
                        )
                        Log.d("restartNavigation", "navigation restarted")

                    }
                }
                delay(duration)
            }
        }

    }

    private fun stopNavigationJob() {

        navigationJob?.cancel()

        locationRepository.stopLocationUpdates()
    }

    private fun startNavigationJobs(goalCoordinates: CoordinatesNavigationDomainModel) {

        startNavigationJob(
            goalCoordinates = goalCoordinates
        ).also {
            startExtrapolationLoop()
        }

    }

    override fun stopNavigationJobs() {

        stopNavigationJob().also {
            stopExtrapolationLoop()
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

                currentLocationState.update {

                    CurrentLocationStateNavigationDomainModel(
                        moved
                    )
                }

                delay(extrapolationDuration)

                extrapolationStartTime = extrapolationStartTime + extrapolationDuration
            }
        }
    }

    private fun stopExtrapolationLoop() {
        extrapolationJob?.cancel()
    }


    private fun restartNavigation(goalCoordinates: CoordinatesNavigationDomainModel) {

        stopNavigationJobs()

        startNavigationJobs(
            goalCoordinates = goalCoordinates
        )

    }

    private suspend fun findNextValidTargetSegment(currentSegmentIndex: Int, proximityDistance: Double, steps: List<RouteStepNavigationDomainModel>): Int {

        return withContext(navigationComputingDispatcher) {

            var passedDistance = routeUtilityClass.haversine(
                startLat = steps[currentSegmentIndex].coordinates.latitude,
                startLon = steps[currentSegmentIndex].coordinates.longitude,
                endLat = steps[currentSegmentIndex+1].coordinates.latitude,
                endLon = steps[currentSegmentIndex+1].coordinates.longitude
            )
            var index = currentSegmentIndex + 1

            while (passedDistance < proximityDistance && steps[index].instruction == null) {

                passedDistance+= routeUtilityClass.haversine(
                    startLat = steps[index].coordinates.latitude,
                    startLon = steps[index].coordinates.longitude,
                    endLat = steps[index+1].coordinates.latitude,
                    endLon = steps[index+1].coordinates.longitude
                )
                index++
            }

            return@withContext index
        }
    }

    fun onNewMatchedLocation(newLocation: CoordinatesNavigationDomainModel) {

        extrapolationStart = lastKnownLocation
        extrapolationEnd = newLocation

        extrapolationStartTime = 50

        lastKnownLocation = newLocation
    }

    suspend fun calculateProximityDistance(coordinates0: CoordinatesNavigationDomainModel, coordinates1: CoordinatesNavigationDomainModel): Double {

        return withContext(navigationComputingDispatcher) {

            val distance = routeUtilityClass.haversine(
                startLat = coordinates0.latitude,
                startLon = coordinates0.longitude,
                endLat = coordinates1.latitude,
                endLon = coordinates1.longitude
            )

            return@withContext when(distance < navigationProximityDistance) {
                true -> navigationProximityDistance
                else -> distance * 2
            }
        }
    }

    private suspend fun moveAlongSegment(
        from: CoordinatesNavigationDomainModel,
        to: CoordinatesNavigationDomainModel,
        secondsAhead: Double
    ): CoordinatesNavigationDomainModel {

        return withContext(navigationComputingDispatcher) {

            val dx = to.longitude - from.longitude
            val dy = to.latitude - from.latitude

            val fraction = (secondsAhead / (duration / 1000)).coerceIn(0.0, 1.0)

            val newLon = from.longitude + fraction * dx
            val newLat = from.latitude + fraction * dy

            return@withContext CoordinatesNavigationDomainModel(
                latitude = newLat,
                longitude = newLon
            )
        }
    }

    private suspend fun matchLocationToSegment(segmentC1: CoordinatesNavigationDomainModel, segmentC2: CoordinatesNavigationDomainModel, location: CoordinatesNavigationDomainModel): CoordinatesNavigationDomainModel {

        return withContext(navigationComputingDispatcher) {

            val ax = segmentC1.longitude
            val ay = segmentC1.latitude
            val bx = segmentC2.longitude
            val by = segmentC2.latitude
            val px = location.longitude
            val py = location.latitude

            val dx = bx - ax
            val dy = by - ay

            if (dx == 0.0 && dy == 0.0) return@withContext segmentC1

            val t = ((px - ax) * dx + (py - ay) * dy) / (dx * dx + dy * dy)
            val clampedT = t.coerceIn(0.0, 1.0)

            val closestX = ax + clampedT * dx
            val closestY = ay + clampedT * dy

            return@withContext CoordinatesNavigationDomainModel(closestY, closestX)
        }
    }

    /** [findClosestSegmentToSnapTo]
     *  find that part of the route (the route Segment) that is the closest to the given point
     *  to which a point should be snapped to during navigation
     *
     *  @param [com.example.model.CoordinatesNavigationDomainModel] of a point that needs to be snapped to a route segment
     *  @param [List] of [com.example.travel_mate.data.RouteStepModel]s from the segment is to be selected
     *  @param [previousIndex] the index of the previous closest [com.example.travel_mate.data.RouteStepModel]
     *
     *  @return a [Pair] of the two endpoints of the found segment
     */
    private suspend fun findClosestSegmentToSnapTo(
        pointToSnap: CoordinatesNavigationDomainModel,
        segments: List<RouteStepNavigationDomainModel>,
        previousIndex: Int
    ) : Pair<RouteStepNavigationDomainModel, RouteStepNavigationDomainModel> {

        return withContext(navigationComputingDispatcher) {
            val reduced = reduceSegmentsToXMostPossible(
                segments = segments,
                closeToIndex = previousIndex,
                threshold = 4
            )

            val closest = findClosestRouteStepModelToPoint(
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
     *  amateur solution to reduce the given [com.example.travel_mate.data.RouteStepModel]s to the [threshold]*2 most possible
     *  to avoid checking the whole list of segments when searching for the closest one during navigation
     *
     *  @param [segments] the whole list of [com.example.travel_mate.data.RouteStepModel]s
     *  @param [closeToIndex] the index of the previous closest [com.example.travel_mate.data.RouteStepModel]
     *  @param [threshold] the number of returned [com.example.travel_mate.data.RouteStepModel]s after and before the [closeToIndex]
     *
     *  @return the reduced list
     */
    private suspend fun reduceSegmentsToXMostPossible(segments: List<RouteStepNavigationDomainModel>, closeToIndex: Int, threshold: Int): List<RouteStepNavigationDomainModel> {

        return withContext(navigationComputingDispatcher) {
            val reduced: MutableList<RouteStepNavigationDomainModel> = mutableListOf()

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

    /** [findClosestRouteStepModelToPoint]
     *  find the closest [com.example.travel_mate.data.RouteStepModel] to the given [com.example.model.CoordinatesNavigationDomainModel]
     *
     *  @param [List] of [com.example.travel_mate.data.RouteStepModel]s
     *  @param [com.example.model.CoordinatesNavigationDomainModel] of the point of which closest [com.example.travel_mate.data.RouteStepModel] is needed
     *
     *  @return the closest [com.example.travel_mate.data.RouteStepModel]
     */
    private suspend fun findClosestRouteStepModelToPoint(segments: List<RouteStepNavigationDomainModel>, point: CoordinatesNavigationDomainModel): RouteStepNavigationDomainModel {

        return withContext(navigationComputingDispatcher) {

            return@withContext segments.minBy {
                routeUtilityClass.haversine(
                    startLat = it.coordinates.latitude,
                    startLon = it.coordinates.longitude,
                    endLat = point.latitude,
                    endLon = point.longitude
                )
            }
        }
    }

    /** [findDistanceBetweenPointAndLine]
     *  calculate the distance of a point and a line defined by 2 other points
     *
     *  @param [com.example.model.CoordinatesNavigationDomainModel] of the start of the line
     *  @param [com.example.model.CoordinatesNavigationDomainModel] of the end of the line
     *  @param [com.example.model.CoordinatesNavigationDomainModel] of the point
     *
     *  @return the distance between the line and the point
     */
    private suspend fun findDistanceBetweenPointAndLine(
        startOfLine: CoordinatesNavigationDomainModel,
        endOfLine: CoordinatesNavigationDomainModel,
        point: CoordinatesNavigationDomainModel
    ): Double {

        return withContext(navigationComputingDispatcher) {

            val dLat = endOfLine.latitude - startOfLine.latitude
            val dLon = endOfLine.longitude - startOfLine.longitude

            val upperPart = abs(
                (dLat * point.longitude) - (dLon * point.latitude)
                        + (endOfLine.longitude * startOfLine.latitude)
                        - (endOfLine.latitude * startOfLine.longitude)
            )
            val lowerPart = sqrt((dLat * dLat) + (dLon * dLon))

            return@withContext upperPart / lowerPart
        }
    }

    override suspend fun updateCurrentLocation(): CoordinatesNavigationDomainModel?{

        return withContext(navigationCoroutineDispatcher) {

            val location = locationRepository.updateCurrentLocation()

            if (location != null) {
                return@withContext CoordinatesNavigationDomainModel(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            }
            return@withContext null
        }
    }
}
package com.example.travel_mate

import android.util.Log
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
import kotlin.coroutines.coroutineContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class RouteRepository constructor(

    private val routeRemoteDataSource: RouteRemoteDataSource,
    private val locationLocalDataSource: LocationLocalDataSource
) {
    private val _routeState = MutableStateFlow(RouteState())
    val routeState: StateFlow<RouteState> = _routeState.asStateFlow()

    private val _navigationState = MutableStateFlow(NavigationState())
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    private val navigationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var navigationJob: Job? = null
    private var extrapolationJob: Job? = null

    private val duration = 3000L
    private var extrapolationDuration: Long = 50 //ms
    private var lastKnownLocation: Coordinates = Coordinates()

    private var predictionPointCurrent: Coordinates = Coordinates()
    private var predictionPointPrevious: Coordinates = Coordinates()

    private var extrapolationStart: Coordinates = lastKnownLocation
    private var extrapolationEnd: Coordinates = lastKnownLocation
    private var extrapolationDistance: Double = 0.0
    private var extrapolationStartTime: Long = 50

    suspend fun setRouteTransportMode(index: Int) {
        withContext(Dispatchers.IO) {

            val mode = when (index) {
                0 -> "foot-walking" // walk
                1 -> "driving-car" // car
                else -> "null"

            }

            _routeState.update {

                it.copy(
                    route = it.route.setTransportMode(
                        transportMode = mode
                    )
                )
            }
        }
    }

    suspend fun initNewRoute(startPlace: Place) {

        withContext(Dispatchers.IO) {

            _routeState.update {

                it.copy(
                    route = Route().addRouteNode(
                        routeNode = RouteNode(
                            name = startPlace.getName(),
                            coordinate = startPlace.getCoordinates(),
                            placeUUID = startPlace.uUID,
                            walkDistance = 0,
                            walkDuration = 0
                        )
                    )
                )
            }
        }
    }

    suspend fun addRemovePlaceToRoute(place: Place) {

        withContext(Dispatchers.IO) {

            when (place.isContainedByRoute()) {
                false -> {
                    addStopToRoute(
                        placeUUID = place.uUID,
                        name = place.getName().toString(),
                        coordinates = place.getCoordinates()
                    )
                }
                true -> {
                    updateStopOfRoute(
                        placeUUID = place.uUID
                    )
                }
            }

        }
    }

    suspend fun resetRoute() {

        withContext(Dispatchers.IO) {

            _routeState.update {

                it.copy(
                    route = Route()
                )
            }

        }
    }

    suspend fun  resetRouteDetails() {

        withContext(Dispatchers.IO) {

            _routeState.update {

                it.copy(
                    route = it.route.setRouteNodes(

                        routeNodes = arrayListOf(it.route.getRouteNodes().first())
                    )
                )
            }
        }
    }

    suspend fun addStopToRoute(placeUUID: String, name: String, coordinates: Coordinates) {

        withContext(Dispatchers.IO) {

            if (_routeState.value.route.getRouteNodes().isNotEmpty()) {

                val lastNode = _routeState.value.route.getLastRouteNode()

                val newRoute = routeRemoteDataSource.getRouteNode(
                    pointStart = lastNode?.coordinate!!,
                    pointEnd = coordinates
                )

                _routeState.update {

                    it.copy(

                        route = it.route.addRouteNode(
                            routeNode = newRoute.apply {
                                this.placeUUID = placeUUID
                                this.name = name
                            }
                        )
                    )
                }
            }
        }
    }

    suspend fun updateStopOfRoute(placeUUID: String) {

        withContext(Dispatchers.IO) {

            val currentNode = _routeState.value.route.getNodeByUUID(
                uuid = placeUUID
            )

            if (currentNode != null) {

                val leftNode = _routeState.value.route.getLeftNeighborOfNode(currentNode)
                val rightNode = _routeState.value.route.getRightNeighborOfNode(currentNode)

                if (rightNode != null) {

                    _routeState.update {

                        it.copy(
                            route = setNewPolyLineToNode(
                                it.route,
                                prevNode = leftNode,
                                node = rightNode
                            )
                        )
                    }
                }
                removeStopFromRoute(
                    uuid = placeUUID
                )
            }
        }
    }

    suspend fun reorderRoute(newPosition: Int, nodeToMove: RouteNode) {

        withContext(Dispatchers.IO) {

            val leftOfNodeAtOldPos = _routeState.value.route.getLeftNeighborOfNode(
                node = nodeToMove
            )
            val rightOfNodeAtOldPos = _routeState.value.route.getRightNeighborOfNode(
                node = nodeToMove
            )

            var newRoute = _routeState.value.route.move(
                position = newPosition,
                node = nodeToMove
            )

            val leftOfNodeToMove = newRoute.getLeftNeighborOfNode(
                node = nodeToMove
            )
            val rightOfNodeToMove = newRoute.getRightNeighborOfNode(
                node = nodeToMove
            )

            if (rightOfNodeAtOldPos != null)
                newRoute = setNewPolyLineToNode(
                    route = newRoute,
                    prevNode = leftOfNodeAtOldPos,
                    node = rightOfNodeAtOldPos
                )


            newRoute = setNewPolyLineToNode(
                route = newRoute,
                prevNode = leftOfNodeToMove,
                node = nodeToMove
            )

            if (rightOfNodeToMove != null)
                newRoute = setNewPolyLineToNode(
                    route = newRoute,
                    prevNode = nodeToMove,
                    node = rightOfNodeToMove
                )

            _routeState.update {

                it.copy(
                    route = newRoute
                )
            }

        }
    }

    suspend fun setNewPolyLineToNode(route :Route, prevNode: RouteNode, node: RouteNode): Route {

        return withContext(Dispatchers.IO) {

            val newRoute = getRouteNode(
                stop1 = prevNode,
                stop2 = node
            )

            return@withContext route.updateRouteByUUID(node.placeUUID.toString(),newRoute)
        }

    }

    suspend fun getRouteNode(stop1: RouteNode, stop2: RouteNode): RouteNode {

        return withContext(Dispatchers.IO) {
            return@withContext routeRemoteDataSource.getRouteNode(
                pointStart = stop1.coordinate!!,
                pointEnd = stop2.coordinate!!
            )
        }
    }

    suspend fun removeStopFromRoute(uuid: String) {

        withContext(Dispatchers.IO) {

            _routeState.update {
                it.copy(

                    route = it.route.removeRouteNodeByUUID(
                        uuid = uuid
                    )
                )
            }
        }
    }

    fun startNavigation() {

        if (navigationJob?.isActive == true) return

        navigationJob = navigationScope.launch {

            locationLocalDataSource.startContinuousLocationUpdates()

            //The location of the user at the start of the navigation
            val initialLocation = getCurrentLocation() ?: _routeState.value.route.getRouteNodes()[0].coordinate!!

            //create a node representing the initial location of the user
            //this is the start point of the navigation
            val currentLocationNode = RouteNode(
                coordinate = initialLocation
            )

            //find the route between the goal of the navigation and the start point
            val navigationRouteNode = getRouteNode(
                stop1 = currentLocationNode,
                stop2 = _routeState.value.route.getRouteNodes()[1]
            )
            //select the appropriate route based on the selected transport mode
            var currentRoute = when(_routeState.value.route.getTransportMode()) {
                "driving-car" -> navigationRouteNode.carRouteSteps
                else -> navigationRouteNode.walkRouteSteps
            }

            lastKnownLocation = initialLocation
            /*
            update the last known location of the user
            if the initial location is null then use the coordinates of the start point
            TODO That is actually the same that is an issue
             */
            onNewMatchedLocation(initialLocation)

            //start the interpolation loop
            startExtrapolationLoop()

            //initial increase count, last distance ant the target segment index
            var distanceIncreaseCount = 0
            var lastDistance = Double.MAX_VALUE
            var targetSegmentIndex = 1

            //update the UI with the initial values
            _navigationState.update {
                it.copy(
                    navigationGoal = navigationRouteNode,
                    currentRouteStep = currentRoute[0]
                )
            }
            //in every 'duration' milliseconds (currently 1500)
            while (isActive) {

                //get the users current location
                val userLocation = updateCurrentLocation() ?: continue

                Log.d("updateCurrentLocation", userLocation.getLatitude().toString() + " " + userLocation.getLongitude().toString())

                //initialize the projected position
                var projectedPosition = matchLocationToSegment(
                    segmentC1 = currentRoute[targetSegmentIndex].coordinates,
                    segmentC2 = currentRoute[targetSegmentIndex - 1].coordinates,
                    location = userLocation
                )

                //determine the distance between the last known location and the
                // target segment
                val currentDistance = haversine(
                    startLat = projectedPosition.getLatitude(),
                    startLon = projectedPosition.getLongitude(),
                    endLat = currentRoute[targetSegmentIndex].coordinates.getLatitude(),
                    endLon = currentRoute[targetSegmentIndex].coordinates.getLongitude()
                )

                //update the interpolation loop
                onNewMatchedLocation(projectedPosition)

                // Check for proximity
                //if the distance is less than 25 meters
                if (currentDistance < 0.030) {

                    //if the target segment is not the last one
                    if ( targetSegmentIndex < currentRoute.size - 1 ) {

                        //find the first segment after the current one that has a
                        //instruction attached to it
                        val nextInstructionStepIndex =
                            findNextInstruction(targetSegmentIndex, currentRoute)

                        if (nextInstructionStepIndex - 1 == targetSegmentIndex) {

                            //update the StateFlow with the found instruction
                            //update the previous instruction with the current
                            //and update the current with the newly found one
                            _navigationState.update {

                                it.copy(
                                    prevRouteStep = it.currentRouteStep,
                                    currentRouteStep = currentRoute[nextInstructionStepIndex]
                                )
                            }
                        }

                        // Move to next segment
                        targetSegmentIndex++

                        lastDistance = Double.MAX_VALUE // reset
                        distanceIncreaseCount = 0

                        Log.d("restartNavigation", "count reset")
                    }
                    //if the distance is greater than 25 meters
                } else {

                    //determine the actual distance between the user and the target segment
                    val actualDistance = haversine(
                        startLat = userLocation.getLatitude(),
                        startLon = userLocation.getLongitude(),
                        endLat = currentRoute[targetSegmentIndex].coordinates.getLatitude(),
                        endLon = currentRoute[targetSegmentIndex].coordinates.getLongitude()
                    )

                    var avgDistance = ((actualDistance * 0.6) + (currentDistance * 1.4 )) / 2

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

                        restartNavigation()
                        Log.d("restartNavigation", "navigation restarted")

                    }
                }
                delay(duration)
            }
        }

    }

    fun stopNavigation() {
        navigationJob?.cancel()

        locationLocalDataSource.stopLocationUpdates()

        _navigationState.update {

            it.copy(
                navigationGoal = null,
                prevRouteStep = null,
                currentRouteStep = null,
                currentLocation = null
            )
        }

    }

    fun restartNavigation() {

        stopNavigation()
        stopExtrapolationLoop()

        startNavigation()

    }

    fun onNewMatchedLocation(newLocation: Coordinates) {
        // Just update the interpolation values; coroutine will pick it up

        extrapolationStart = lastKnownLocation
        extrapolationEnd = newLocation

        extrapolationStartTime = 50

        lastKnownLocation = newLocation
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

    fun stopExtrapolationLoop() {
        extrapolationJob?.cancel()
    }

    fun moveAlongSegment(
        from: Coordinates,
        to: Coordinates,
        secondsAhead: Double
    ): Coordinates {

        val dx = to.getLongitude() - from.getLongitude()
        val dy = to.getLatitude() - from.getLatitude()

        val fraction = (secondsAhead / (duration / 1000) ).coerceIn(0.0, 1.0)

        val newLon = from.getLongitude() + fraction * dx
        val newLat = from.getLatitude() + fraction * dy

        return Coordinates(
            latitude = newLat,
            longitude = newLon
        )
    }

    private fun matchLocationToSegment(segmentC1: Coordinates, segmentC2: Coordinates, location: Coordinates): Coordinates {

        val ax = segmentC1.getLongitude()
        val ay = segmentC1.getLatitude()
        val bx = segmentC2.getLongitude()
        val by = segmentC2.getLatitude()
        val px = location.getLongitude()
        val py = location.getLatitude()

        val dx = bx - ax
        val dy = by - ay

        if (dx == 0.0 && dy == 0.0) return segmentC1 // a == b

        val t = ((px - ax) * dx + (py - ay) * dy) / (dx * dx + dy * dy)
        val clampedT = t.coerceIn(0.0, 1.0) // ensures projection is on the segment

        val closestX = ax + clampedT * dx
        val closestY = ay + clampedT * dy

        return Coordinates(closestY, closestX)

    }

    fun findNextInstruction(currentRouteStepIndex: Int, routeSteps: List<RouteStep>): Int {

        val nextInstruction = routeSteps.first { routeSteps.indexOf(it) > currentRouteStepIndex && it.instruction != null }

        return routeSteps.indexOf(nextInstruction)
    }

    suspend fun optimizeRoute() {

        CoroutineScope(coroutineContext).launch {

            val currentRoute = _routeState.value.route
            val currentRouteNodes = currentRoute.getRouteNodes()

            var newRouteNodes: List<RouteNode> = emptyList()

            var initialMatrix: ArrayList<ArrayList<Double>> = ArrayList()

            for (i in 0 until currentRouteNodes.size) {
                val row = ArrayList<Double>(currentRouteNodes.size)
                for (j in 0 until currentRouteNodes.size) {
                    row.add(0.0)
                }
                initialMatrix.add(row)
            }

            for (i in 0 until currentRouteNodes.size) {
                for (j in 0 until currentRouteNodes.size) {
                    initialMatrix[i][j] =
                        haversine(
                            startLat = currentRouteNodes[i].coordinate!!.getLatitude(),
                            startLon = currentRouteNodes[i].coordinate!!.getLongitude(),
                            endLat = currentRouteNodes[j].coordinate!!.getLatitude(),
                            endLon = currentRouteNodes[j].coordinate!!.getLongitude()
                        )
                }
                currentRouteNodes[i].matrixIndex = i
            }

            newRouteNodes = nearestAddition(
                routeNodes = currentRouteNodes,
                distanceMatrix = initialMatrix
            )

            newRouteNodes = twoOpt(
                routeNodes = newRouteNodes,
                distanceMatrix = initialMatrix
            )

            var newRoute = currentRoute.copy(
                routeNodes = listOf(newRouteNodes[0])
            )

            newRouteNodes.forEachIndexed { index, node ->
                if (index>0) {
                    val lastNode = newRoute.getLastRouteNode()

                    val plusRoute = routeRemoteDataSource.getRouteNode(
                        pointStart = lastNode?.coordinate!!,
                        pointEnd = node.coordinate!!
                    )

                    newRoute = newRoute.addRouteNode(
                        routeNode = plusRoute.apply {
                            placeUUID = node.placeUUID
                            name = node.name
                        }
                    )
                }
            }

            _routeState.update {

                it.copy(
                    route = newRoute
                )
            }
        }
    }

    private suspend fun nearestAddition(routeNodes: List<RouteNode>, distanceMatrix: ArrayList<ArrayList<Double>>): List<RouteNode> {

        return withContext(Dispatchers.IO) {

            var newRouteNodes: MutableList<RouteNode> = mutableListOf(routeNodes[0])

            do {

                var selected: RouteNode = routeNodes.last()
                var minDistance = Double.MAX_VALUE

                for (i in 0 until distanceMatrix.size - 1) {
                    for (j in i + 1 until distanceMatrix[i].size) {

                        if (distanceMatrix[i][j] < minDistance && !newRouteNodes.contains(routeNodes[j])) {

                            selected = routeNodes[j]
                            minDistance = distanceMatrix[i][j]
                        }
                    }
                }

                selected.approxDist = minDistance
                newRouteNodes.add(selected)

            } while (newRouteNodes.size < routeNodes.size)

            return@withContext newRouteNodes
        }

    }

    private suspend fun twoOpt(routeNodes: List<RouteNode>, distanceMatrix: ArrayList<ArrayList<Double>>): List<RouteNode> {

        return withContext(Dispatchers.IO) {

            var improved = true
            val newRoute = routeNodes.toMutableList()

            while (improved) {
                improved = false
                for (i in 1 until newRoute.size - 2) {
                    for (j in i + 1 until newRoute.size - 1) {

                        val a = newRoute[i - 1]
                        val b = newRoute[i]
                        val c = newRoute[j]
                        val d = newRoute[j + 1]

                        val currentDist =
                            distanceMatrix[a.matrixIndex][b.matrixIndex] + distanceMatrix[c.matrixIndex][d.matrixIndex]
                        val newDist =
                            distanceMatrix[a.matrixIndex][c.matrixIndex] + distanceMatrix[b.matrixIndex][d.matrixIndex]

                        if (newDist < currentDist) {
                            newRoute.subList(i, j + 1).reverse()
                            improved = true
                        }
                    }
                }
            }

            return@withContext newRoute

        }
    }

    private fun haversine(startLat: Double, startLon: Double, endLat: Double, endLon: Double): Double {
        var lat1 = Math.toRadians(startLat)
        var lon1 = Math.toRadians(startLon)
        var lat2 = Math.toRadians(endLat)
        var lon2 = Math.toRadians(endLon)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1) * cos(lat2) * sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val earthRadius = 6371.0
        val distance = earthRadius * c

        return distance
    }

    /** [getCurrentLocation]
     *  ask for a location update, generate a [Coordinates] from it and return
     */
    suspend fun getCurrentLocation(): Coordinates?{

        return withContext(Dispatchers.IO) {

            val location = locationLocalDataSource.getCurrentLocation()

            if (location != null) {
                return@withContext Coordinates(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            }
            return@withContext null
        }
    }

    suspend fun updateCurrentLocation(): Coordinates?{

        return withContext(Dispatchers.IO) {

            val location = locationLocalDataSource.updateCurrentLocation()

            if (location != null) {
                return@withContext Coordinates(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            }
            return@withContext null
        }
    }

    data class RouteState( val route: Route = Route()
    )

    data class NavigationState( val navigationGoal: RouteNode? = null,
                                val currentLocation: Coordinates? = null,
                                val prevRouteStep: RouteStep? = null,
                                val currentRouteStep: RouteStep? = null
    )
}
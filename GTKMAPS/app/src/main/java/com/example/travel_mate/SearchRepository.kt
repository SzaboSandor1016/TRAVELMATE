package com.example.travel_mate

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class SearchRepository /*@Inject*/ constructor(
    private val overpassRemoteDataSource: OverpassRemoteDataSource,
    private val photonRemoteDataSource: PhotonRemoteDataSource,
    private val routeRemoteDataSource: RouteRemoteDataSource
    ) {

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _searchOptions = MutableStateFlow(SearchOptions())
    val searchOptions: StateFlow<SearchOptions> = _searchOptions.asStateFlow()


    suspend fun setSearchStartPlace(startPlace: Place) {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(
                    search = it.search.setStartPlace(
                        startPlace = startPlace
                    )
                )
            }
        }
    }

    suspend fun setTransportMode(index: Int) {

        CoroutineScope(coroutineContext).launch {

            setRouteTransportMode(
                index = index
            )

            setSearchTransportMode(
                index = index
            )

        }
    }
    suspend fun setRouteTransportMode(index: Int) {
        withContext(Dispatchers.IO) {

            val mode = when (index) {
                0 -> "foot-walking" // walk
                1 -> "driving-car" // car
                else -> "null"

            }

            _searchState.update {

                it.copy(
                    route = it.route.setTransportMode(
                        transportMode = mode
                    )
                )
            }
        }
    }

    suspend fun setSearchTransportMode(index: Int) {
        withContext(Dispatchers.IO) {

            val mode = when (index) {
                0 -> "walk" // walk
                1 -> "car" // car
                else -> null

            }

            _searchOptions.update {

                it.copy(
                    transportMode = mode
                )
            }
        }
    }

    suspend fun setMinute(index: Int) {

        withContext(Dispatchers.IO) {

            val minute = when (index) {
                0 -> 15
                1 -> 30
                2 -> 45
                else -> 0

            }
            _searchOptions.update {

                it.copy(
                    minute = minute
                )
            }
        }
    }

    suspend fun  resetSearchDetails() {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(

                    search = it.search.setStartPlace(
                        startPlace = it.search.getStartPlace()
                    ),
                    route = Route().addRouteNode(
                        routeNode = RouteNode(
                            name = it.search.getStartPlace()?.getName(),
                            coordinate = it.search.getStartPlace()?.getCoordinates(),
                            placeUUID = it.search.getStartPlace()?.uUID,
                            walkDistance = 0,
                            walkDuration = 0
                        )
                    )
                )
            }

        }
    }



    suspend fun  resetFullSearchDetails() {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(
                    search = Search(),
                    route = Route()
                )
            }

        }
    }

    suspend fun  resetRouteDetails() {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(
                    route = it.route.setRouteNodes(

                        routeNodes = arrayListOf(it.route.getRouteNodes().first())
                    )
                )
            }
            clearPlacesAddedToRoute()
        }
    }

    suspend fun initNewSearch(startPlace: Place) {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(
                    search = Search().setStartPlace(
                        startPlace = startPlace
                    ),
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

    suspend fun initNewSearchFromTrip(startPlace: Place, places: List<Place>) {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(
                    search = Search(
                        startPlace = startPlace,
                        places = places
                    ),
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

    suspend fun getCurrentPlaceByUUID(uuid: String){

        withContext(Dispatchers.IO) {

            val currentPlace = _searchState.value.search.getPlaceByUUID(uuid)!!

            _searchState.update {

                it.copy(
                    currentPlace = currentPlace
                )
            }
        }
    }
    fun getStartPlace(): Place? {
        return _searchState.value.search.getStartPlace()
    }

    fun getPlacesContainedByTrip(): List<Place> {

        return _searchState.value.search.getPlacesContainedByTrip()
    }

    suspend fun addRemovePlaceToTrip(uuid: String){

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(
                    search = it.search.selectPlaceByUUIDForTrip(
                        uuid = uuid
                    ),
                    currentPlace = it.currentPlace.containedByTrip(
                        contained = !it.currentPlace.isContainedByTrip()
                    )
                )
            }
        }
    }

    suspend fun addRemovePlaceToRoute(uuid: String){

        withContext(Dispatchers.IO) {

            val place = _searchState.value.search.getPlaceByUUID(
                uuid = uuid)!!

            when (place.isContainedByRoute()) {
                false -> {
                    addStopToRoute(
                        place = place
                    )
                }
                true -> {
                    updateStopOfRoute(
                        place = place
                    )
                }
            }

            if (place.uUID == _searchState.value.currentPlace.uUID)
                _searchState.update {

                    it.copy(
                        currentPlace = it.currentPlace.containedByRoute(
                            contained = !it.currentPlace.isContainedByRoute()
                        )
                    )
                }

            _searchState.update {

                it.copy(
                    search = it.search.selectPlaceByUUIDForRoute(
                        uuid = uuid
                    )
                )
            }
        }
    }

    suspend fun clearPlacesAddedToTrip() {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(
                    search = it.search.clearPlacesAddedToTrip()
                )
            }
        }

    }
    suspend fun clearPlacesAddedToRoute() {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(
                    search = it.search.copy().clearPlacesAddedToRoute()
                )
            }
        }

    }

    suspend fun removePlacesByCategory(category: String) {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(

                    search = it.search.setPlaces(it.search.getPlaces().filter { it.getCategory() != category })
                )
            }
        }
    }


    suspend fun fetchPlaces(content: String, lat: String, lon: String, city: String, category: String) {

        withContext(Dispatchers.IO) {

            val places = when (_searchOptions.value.distance) {
                0.0 -> {
                    overpassRemoteDataSource.fetchPlacesByCity(
                        content = content,
                        city = city,
                        category = category
                    )

                }
                else -> {
                    overpassRemoteDataSource.fetchPlacesByCoordinates(
                        content = content,
                        lat = lat,
                        lon = lon,
                        dist = _searchOptions.value.distance,
                        category = category
                    )
                }
            }

            _searchState.update {

                it.copy(

                    search = it.search.addPlaces(places)
                )
            }
        }
    }


    suspend fun getStartPlaces(query: String): List<Place> {

        return photonRemoteDataSource.getStartPlaces(query)
    }

    suspend fun getReverseGeoCode(coordinates: Coordinates) {

        withContext(Dispatchers.IO) {

            try {
                val reverseGeoCode = async { photonRemoteDataSource.getReverseGeoCode(
                    coordinates = coordinates
                ) }

                val reverseGeoResult = reverseGeoCode.await()

                if (reverseGeoResult.isNotEmpty() == true) {
                    initNewSearch(reverseGeoResult[0])
                } else {

                }

            } catch (e: Error) {
                Log.e("FindUserLocation", "get user location: error")
            }

        }
    }

    suspend fun addStopToRoute(place: Place) {

        withContext(Dispatchers.IO) {

            if (_searchState.value.route.getRouteNodes().isNotEmpty()) {

                val lastNode = _searchState.value.route.getLastRouteNode()

                val newRoute = routeRemoteDataSource.getRouteNode(
                    pointStart = lastNode?.coordinate!!,
                    pointEnd = place.getCoordinates()
                )

                _searchState.update {

                    it.copy(

                        route = it.route.addRouteNode(
                            routeNode = newRoute.apply {
                                placeUUID = place.uUID
                                name = place.getName()
                            }
                        )
                    )
                }
            }
        }
    }

    suspend fun updateStopOfRoute(place: Place) {

        withContext(Dispatchers.IO) {

            val currentNode = _searchState.value.route.getNodeByUUID(
                uuid = place.uUID
            )

            if (currentNode != null) {

                val leftNode = _searchState.value.route.getLeftNeighborOfNode(currentNode)
                val rightNode = _searchState.value.route.getRightNeighborOfNode(currentNode)

                if (rightNode != null) {

                    _searchState.update {

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
                    uuid = place.uUID
                )
            }
        }
    }

    suspend fun reorderRoute(newPosition: Int, nodeToMove: RouteNode) {

        withContext(Dispatchers.IO) {

            val leftOfNodeAtOldPos = searchState.value.route.getLeftNeighborOfNode(
                node = nodeToMove
            )
            val rightOfNodeAtOldPos = searchState.value.route.getRightNeighborOfNode(
                node = nodeToMove
            )

            var newRoute = _searchState.value.route.move(
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

            _searchState.update {

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

            _searchState.update {
                it.copy(

                    route = it.route.removeRouteNodeByUUID(
                        uuid = uuid
                    )
                )
            }
        }
    }

    suspend fun optimizeRoute() {

        CoroutineScope(coroutineContext).launch {

            val currentRoute = _searchState.value.route
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

            _searchState.update {

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

    data class SearchState(val search: Search = Search(),
                           val route: Route = Route(),
                           val currentPlace: Place = Place()
    )

    data class SearchOptions(var transportMode: String? = null,
                             var minute: Int = 0
    ) {

        private var speed = when (transportMode) {
            "walk" -> 3500 // walk
            "car" -> 50000 // car
            else -> 0
        }

        var distance = (this.speed.times(this.minute)) / 60.0
    }

}
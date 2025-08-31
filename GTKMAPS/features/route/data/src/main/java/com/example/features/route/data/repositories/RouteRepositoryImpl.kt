package com.example.features.route.data.repositories

import android.util.Log
import com.example.features.route.data.mappers.toFlowOfRouteInfo
import com.example.features.route.data.mappers.toFlowOfRouteMapData
import com.example.features.route.domain.models.CoordinatesRouteDomainModel
import com.example.features.route.domain.models.PlaceRouteDomainModel
import com.example.features.route.domain.models.RouteNodeRouteDomainModel
import com.example.features.route.domain.models.RouteRouteDomainModel
import com.example.features.route.domain.models.RouteStateRouteDomainModel
import com.example.features.route.domain.models.info.RouteInfoRouteDomainModel
import com.example.features.route.domain.models.mapdata.RouteMapDataRouteDomainModel
import com.example.features.route.domain.repositories.RouteNodeRepository
import com.example.features.route.domain.repositories.RouteRepository
import com.example.core.utils.RouteUtilityClass
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import org.osmdroid.views.overlay.Polyline
import kotlin.collections.toMutableList

class RouteRepositoryImpl constructor(
): RouteRepository {

    private val routeNodeRepository: RouteNodeRepository by inject(RouteNodeRepository::class.java)

    private val routeCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val routeComputingDispatcher: CoroutineDispatcher = Dispatchers.Default

    private val routeUtilityClass = RouteUtilityClass()

    private val _routeState = MutableStateFlow(RouteStateRouteDomainModel())
    override val routeState: StateFlow<RouteStateRouteDomainModel> = _routeState.asStateFlow()

    override fun getRouteInfo(): Flow<RouteInfoRouteDomainModel> {
        return _routeState.toFlowOfRouteInfo()
    }

    override fun getRouteMapData(): Flow<RouteMapDataRouteDomainModel> {
        return _routeState.toFlowOfRouteMapData()
    }

    override fun isPlaceContained(uuid: String): Flow<Boolean> {

        return _routeState.map { routeState ->
            routeState.route.routeNodes.any { it.placeUUID == uuid }
        }
    }

    override suspend fun testSetRouteTransportMode(index: Int) {

        withContext(routeCoroutineDispatcher) {

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

    override suspend fun testResetRoute(all: Boolean) {

        withContext(routeCoroutineDispatcher) {

            when (all) {

                true -> _routeState.update {

                    it.copy(
                        route = RouteRouteDomainModel()
                    )
                }

                false -> _routeState.update {

                    it.copy(
                        route = it.route.setRouteNodes(

                            routeNodes = arrayListOf(it.route.routeNodes.first())
                        )
                    )
                }
            }

        }
    }

    override fun getCurrentRouteNodesCoordinates(): List<CoordinatesRouteDomainModel> = _routeState.value.route.routeNodes.map { it.coordinate }

    override suspend fun setRouteTransportMode(index: Int) {
        withContext(routeCoroutineDispatcher) {

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

    override fun getRouteTransportMode(): String {

        return _routeState.value.route.transportMode
    }

    override suspend fun initNewRoute(startPlace: PlaceRouteDomainModel) {

        withContext(routeCoroutineDispatcher) {

            Log.d("initRouteName", startPlace.name.toString())

            _routeState.update {

                it.copy(
                    route = RouteRouteDomainModel().addRouteNode(
                        routeNode = RouteNodeRouteDomainModel(
                            walkPolyLine = Polyline(),
                            carPolyLine = Polyline(),
                            name = startPlace.name,
                            coordinate = startPlace.coordinates,
                            placeUUID = startPlace.uUID
                        )
                    )
                )
            }
        }
    }

    override suspend fun addRemovePlace(place: PlaceRouteDomainModel) {

        withContext(routeCoroutineDispatcher) {

            if (_routeState.value.route.routeNodes.none { it.placeUUID == place.uUID }) {

                Log.d("routeStopAddedName", place.name.toString())

                addStopToRoute(
                    placeUUID = place.uUID,
                    name = place.name.toString(),
                    coordinates = place.coordinates
                )
            } else {
                updateStopOfRoute(
                    placeUUID = place.uUID
                )
            }
        }
    }

    override suspend fun removePlaceFromRouteByUUID(placeUUID: String) {

        updateStopOfRoute(
            placeUUID = placeUUID
        )
    }

    override suspend fun resetRoute(all: Boolean) {

        withContext(routeCoroutineDispatcher) {

            when (all) {

                true -> _routeState.update {

                    it.copy(
                        route = RouteRouteDomainModel()
                    )
                }

                false -> _routeState.update {

                    it.copy(
                        route = it.route.setRouteNodes(

                            routeNodes = arrayListOf(it.route.routeNodes.first())
                        )
                    )
                }
            }

        }
    }

    suspend fun addStopToRoute(placeUUID: String, name: String, coordinates: CoordinatesRouteDomainModel) {

        withContext(routeCoroutineDispatcher) {

            if (_routeState.value.route.routeNodes.isNotEmpty()) {

                val lastNode = _routeState.value.route.routeNodes.last()

                val newRoute = routeNodeRepository.getRouteNode(
                    placeUUID = placeUUID,
                    stop1 = lastNode.coordinate,
                    stop2 = coordinates
                )?: return@withContext

                Log.d("lastNode.name", lastNode.name.toString())

                _routeState.update {

                    it.copy(

                        route = it.route.copy(
                            routeNodes = it.route.routeNodes.plus(
                                newRoute.apply {
                                    this.placeUUID = placeUUID
                                    this.name = name
                                }
                            )
                        )
                    )
                }
            }
        }
    }

    suspend fun updateStopOfRoute(placeUUID: String) {

        withContext(routeCoroutineDispatcher) {

            val currentNode = _routeState.value.route.getNodeByUUID(
                uuid = placeUUID
            )

            if (currentNode != null) {

                val leftNode = _routeState.value.route.getLeftNeighborOfNode(currentNode)
                val rightNode = _routeState.value.route.getRightNeighborOfNode(currentNode)

                if (rightNode != null) {

                    val updatedRoute = setNewPolyLineToNode(
                        _routeState.value.route,
                        prevNode = leftNode,
                        node = rightNode
                    )?: _routeState.value.route

                    _routeState.update {

                        it.copy(
                            route = updatedRoute
                        )
                    }
                }
                removeStopFromRoute(
                    uuid = placeUUID
                )
            }
        }
    }

    override suspend fun reorderRoute(newPosition: Int, placeUUID: String) {

        withContext(routeCoroutineDispatcher) {

            val nodeToMove = _routeState.value.route.routeNodes.find { it.placeUUID == placeUUID }!!

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
            //if it fails to get a new polyline anywhere return and do not commit any changes
            if (rightOfNodeAtOldPos != null)
                newRoute = setNewPolyLineToNode(
                    route = newRoute,
                    prevNode = leftOfNodeAtOldPos,
                    node = rightOfNodeAtOldPos
                )?: newRoute


            newRoute = setNewPolyLineToNode(
                route = newRoute,
                prevNode = leftOfNodeToMove,
                node = nodeToMove
            )?: newRoute

            if (rightOfNodeToMove != null)
                newRoute = setNewPolyLineToNode(
                    route = newRoute,
                    prevNode = nodeToMove,
                    node = rightOfNodeToMove
                )?: newRoute

            _routeState.update {

                it.copy(
                    route = newRoute
                )
            }

        }
    }

    suspend fun setNewPolyLineToNode(route : RouteRouteDomainModel, prevNode: RouteNodeRouteDomainModel, node: RouteNodeRouteDomainModel): RouteRouteDomainModel? {

        return withContext(routeCoroutineDispatcher) {

            return@withContext route.updateRouteByUUID(
                node.placeUUID,
                routeNodeRepository.getRouteNode(
                    placeUUID = node.placeUUID,
                    stop1 = prevNode.coordinate,
                    stop2 = node.coordinate
                )
            )
        }

    }

    private suspend fun removeStopFromRoute(uuid: String) {

        withContext(routeCoroutineDispatcher) {

            _routeState.update {
                it.copy(

                    route = it.route.removeRouteNodeByUUID(
                        uuid = uuid
                    )
                )
            }
        }
    }

    override suspend fun optimizeRoute() {

        withContext(routeComputingDispatcher) {

            val currentRoute = _routeState.value.route
            val currentRouteNodes = currentRoute.routeNodes

            var newRouteNodes: List<RouteNodeRouteDomainModel>

            val initialMatrix = generateInitialDistanceMatrixFrom(
                routeNodes = currentRouteNodes
            )

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

            //if it fails to get a new polyline for any of the route nodes,
            // return and do not change the current route
            newRouteNodes.forEachIndexed { index, node ->
                if (index>0) {
                    val lastNode = newRoute.getLastRouteNode()

                    val plusRoute = routeNodeRepository.getRouteNode(
                        placeUUID = node.placeUUID,
                        stop1 = lastNode?.coordinate!!,
                        stop2 = node.coordinate
                    )?: return@withContext

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

    private suspend fun generateInitialDistanceMatrixFrom(routeNodes: List<RouteNodeRouteDomainModel>): ArrayList<ArrayList<Double>> {

        return withContext(routeComputingDispatcher) {

            val initialMatrix: ArrayList<ArrayList<Double>> = ArrayList()

            for (i in 0 until routeNodes.size) {
                val row = ArrayList<Double>(routeNodes.size)
                for (j in 0 until routeNodes.size) {
                    row.add(0.0)
                }
                initialMatrix.add(row)
            }

            for (i in 0 until routeNodes.size-1) {
                for (j in 0 until routeNodes.size-1) {
                    initialMatrix[i][j] =
                        routeUtilityClass.haversine(
                            startLat = routeNodes[i].coordinate.latitude,
                            startLon = routeNodes[i].coordinate.longitude,
                            endLat = routeNodes[j].coordinate.latitude,
                            endLon = routeNodes[j].coordinate.longitude
                        )
                }
                routeNodes[i].matrixIndex = i
            }

            return@withContext initialMatrix
        }
    }

    private suspend fun nearestAddition(routeNodes: List<RouteNodeRouteDomainModel>, distanceMatrix: ArrayList<ArrayList<Double>>): List<RouteNodeRouteDomainModel> {

        return withContext(routeComputingDispatcher) {

            val newRouteNodes: MutableList<RouteNodeRouteDomainModel> = mutableListOf(routeNodes[0])

            do {

                var selected: RouteNodeRouteDomainModel = routeNodes.last()
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

    private suspend fun twoOpt(routeNodes: List<RouteNodeRouteDomainModel>, distanceMatrix: ArrayList<ArrayList<Double>>): List<RouteNodeRouteDomainModel> {

        return withContext(routeComputingDispatcher) {

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


}
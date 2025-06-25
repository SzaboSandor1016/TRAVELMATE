package com.example.travel_mate.data

import com.example.travel_mate.domain.RouteNodeRepository
import com.example.travel_mate.domain.RouteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class RouteRepositoryImpl constructor(
): RouteRepository {

    private val routeNodeRepository: RouteNodeRepository by inject(RouteNodeRepository::class.java)

    private val _routeState = MutableStateFlow(RouteState())
    override val routeState: StateFlow<RouteState> = _routeState.asStateFlow()

    private val routeCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val routeComputingDispatcher: CoroutineDispatcher = Dispatchers.Default

    private val routeUtilityClass = RouteUtilityClass()

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
                        route = Route()
                    )
                }

                false -> _routeState.update {

                    it.copy(
                        route = it.route.setRouteNodes(

                            routeNodes = arrayListOf(it.route.getRouteNodes().first())
                        )
                    )
                }
            }

        }
    }

    override fun getCurrentRouteNodes(): List<RouteNode> = _routeState.value.route.getRouteNodes()

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

    override suspend fun initNewRoute(startPlace: Place) {

        withContext(routeCoroutineDispatcher) {

            _routeState.update {

                it.copy(
                    route = Route().addRouteNode(
                        routeNode = RouteNode(
                            name = startPlace.getName(),
                            coordinate = startPlace.getCoordinates(),
                            placeUUID = startPlace.uUID
                        )
                    )
                )
            }
        }
    }

    override suspend fun addRemovePlaceToRoute(place: Place) {

        withContext(routeCoroutineDispatcher) {

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

    override suspend fun resetRoute(all: Boolean) {

        withContext(routeCoroutineDispatcher) {

            when (all) {

                true -> _routeState.update {

                    it.copy(
                        route = Route()
                    )
                }

                false -> _routeState.update {

                    it.copy(
                        route = it.route.setRouteNodes(

                            routeNodes = arrayListOf(it.route.getRouteNodes().first())
                        )
                    )
                }
            }

        }
    }

    suspend fun addStopToRoute(placeUUID: String, name: String, coordinates: Coordinates) {

        withContext(routeCoroutineDispatcher) {

            if (_routeState.value.route.getRouteNodes().isNotEmpty()) {

                val lastNode = _routeState.value.route.getLastRouteNode()

                val newRoute = routeNodeRepository.getRouteNode(
                    stop1 = lastNode?.coordinate!!,
                    stop2 = coordinates
                )

                _routeState.update {

                    it.copy(

                        route = it.route.copy().addRouteNode(
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

        withContext(routeCoroutineDispatcher) {

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

    override suspend fun reorderRoute(newPosition: Int, nodeToMove: RouteNode) {

        withContext(routeCoroutineDispatcher) {

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

    suspend fun setNewPolyLineToNode(route : Route, prevNode: RouteNode, node: RouteNode): Route {

        return withContext(routeCoroutineDispatcher) {

            val newRoute = routeNodeRepository.getRouteNode(
                stop1 = prevNode.coordinate!!,
                stop2 = node.coordinate!!
            )

            return@withContext route.updateRouteByUUID(node.placeUUID.toString(), newRoute)
        }

    }

    override suspend fun removeStopFromRoute(uuid: String) {

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
            val currentRouteNodes = currentRoute.getRouteNodes()

            var newRouteNodes: List<RouteNode> = emptyList()

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

            /*var newRoute = currentRoute.copy(
                routeNodes = emptyList()
            )

            for(i in 1 until newRouteNodes.size-1) {

                val plusRoute = routeNodeRepository.getRouteNode(
                    stop1 = newRouteNodes[i-1].coordinate!!,
                    stop2 =  newRouteNodes[i].coordinate!!
                )

                newRoute = newRoute.addRouteNode(
                    routeNode = plusRoute.apply {
                        placeUUID = newRouteNodes[i].placeUUID
                        name = newRouteNodes[i].name
                    }
                )
            }*/

            var newRoute = currentRoute.copy(
                routeNodes = listOf(newRouteNodes[0])
            )

            newRouteNodes.forEachIndexed { index, node ->
                if (index>0) {
                    val lastNode = newRoute.getLastRouteNode()

                    val plusRoute = routeNodeRepository.getRouteNode(
                        stop1 = lastNode?.coordinate!!,
                        stop2 = node.coordinate!!
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

    private suspend fun generateInitialDistanceMatrixFrom(routeNodes: List<RouteNode>): ArrayList<ArrayList<Double>> {

        return withContext(routeComputingDispatcher) {

            var initialMatrix: ArrayList<ArrayList<Double>> = ArrayList()

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
                            startLat = routeNodes[i].coordinate!!.getLatitude(),
                            startLon = routeNodes[i].coordinate!!.getLongitude(),
                            endLat = routeNodes[j].coordinate!!.getLatitude(),
                            endLon = routeNodes[j].coordinate!!.getLongitude()
                        )
                }
                routeNodes[i].matrixIndex = i
            }

            return@withContext initialMatrix
        }
    }

    private suspend fun nearestAddition(routeNodes: List<RouteNode>, distanceMatrix: ArrayList<ArrayList<Double>>): List<RouteNode> {

        return withContext(routeComputingDispatcher) {

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

    data class RouteState(
        val route: Route = Route()
    )
}
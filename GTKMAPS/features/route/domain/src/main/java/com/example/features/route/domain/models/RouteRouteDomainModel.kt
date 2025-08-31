package com.example.features.route.domain.models

import java.io.Serializable

/** [RouteRouteDomainModel]
 * the class of a whole route
 *
 * !!
 * Not to be confused with the route returned by the [com.example.data.datasources.RouteRemoteDataSourceImpl]'s method
 * which is only a [Polyline] that connects two stops
 * !!
 *
 * contains [RouteNodeRouteDomainModel]s that represents stops of a route
 * and a transport mode [String] that is used for showing
 * the duration and distance attributes of both the individual nodes
 * both the sum of all durations and distances for walking OR driving
 *
 * The first element of the [routeNodes] is always the stop generated from the start [com.example.model.Place]
 * of the current [com.example.model.Search]
 */
data class RouteRouteDomainModel(
    val startPlace: PlaceRouteDomainModel? = null,
    val routeNodes: List<RouteNodeRouteDomainModel> = emptyList(),
    val transportMode: String = "foot-walking"
): Serializable {

    val fullWalkDuration: Int get() = routeNodes.sumOf { it.walkDuration }
    val fullWalkDistance: Int get() = routeNodes.sumOf { it.walkDistance }

    val fullCarDuration: Int get() = routeNodes.sumOf { it.carDuration }
    val fullCarDistance: Int get() = routeNodes.sumOf { it.carDistance }

    fun setRouteNodes(routeNodes: ArrayList<RouteNodeRouteDomainModel>): RouteRouteDomainModel{
        return this.copy(routeNodes = routeNodes)
    }
    fun addRouteNode(routeNode: RouteNodeRouteDomainModel): RouteRouteDomainModel{
        return this.copy(routeNodes = this.routeNodes.plus(routeNode))
    }

    fun getNodeByUUID(uuid: String): RouteNodeRouteDomainModel? {

        return this.routeNodes.find { it.placeUUID == uuid }
    }

    fun getLeftNeighborOfNode(node: RouteNodeRouteDomainModel): RouteNodeRouteDomainModel {

        return this.routeNodes[this.routeNodes.indexOf(node)-1]
    }

    fun getRightNeighborOfNode(node: RouteNodeRouteDomainModel): RouteNodeRouteDomainModel? {

        if (this.routeNodes.indexOf(node) != this.routeNodes.size-1)
            return this.routeNodes[this.routeNodes.indexOf(node)+1]

        return null
    }

    fun removeRouteNodeByUUID(uuid: String): RouteRouteDomainModel{
        val node = getNodeByUUID(
            uuid = uuid
        )
        if (node != null)
            return this.copy(routeNodes = this.routeNodes.minus(node))
        return this
    }
    fun updateRouteByUUID(uuid: String, newNode: RouteNodeRouteDomainModel?): RouteRouteDomainModel {

        if (newNode==null)
            return this

        val target = getNodeByUUID(uuid = uuid)

        val replacement = target?.copy().apply {

            this?.walkPolyLine = newNode.walkPolyLine
            this?.walkDuration = newNode.walkDuration
            this?.walkDistance = newNode.walkDistance

            this?.carPolyLine = newNode.carPolyLine
            this?.carDuration = newNode.carDuration
            this?.carDistance = newNode.carDistance
        }

        return this.copy(
            routeNodes = replace(
                list = this.routeNodes,
                target = target!!,
                replacement = replacement!!
            )
        )
    }

    fun move(position: Int,node: RouteNodeRouteDomainModel): RouteRouteDomainModel {

        val mutableList = this.routeNodes.toMutableList()

        mutableList.remove(node)

        mutableList.add(position, node)

        return this.copy(

            routeNodes = mutableList.toList()
        )
    }

    private fun replace(list: List<RouteNodeRouteDomainModel>, target: RouteNodeRouteDomainModel, replacement: RouteNodeRouteDomainModel): List<RouteNodeRouteDomainModel> {

        val mutableList = list.toMutableList()

        val index = mutableList.indexOf(target)

        mutableList.remove(target)

        mutableList.add(index,replacement)

        return mutableList.toList()
    }

    fun getLastRouteNode(): RouteNodeRouteDomainModel? {

        return this.routeNodes.last()
    }

    fun setTransportMode(transportMode: String): RouteRouteDomainModel{
        return this.copy(transportMode = transportMode)
    }
}
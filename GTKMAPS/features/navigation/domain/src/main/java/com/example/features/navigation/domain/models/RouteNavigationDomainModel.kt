package com.example.features.navigation.domain.models

import java.io.Serializable

/** [RouteNavigationDomainModel]
 * the class of a whole route
 *
 * !!
 * Not to be confused with the route returned by the [com.example.data.datasources.RouteRemoteDataSourceImpl]'s method
 * which is only a [Polyline] that connects two stops
 * !!
 *
 * contains [RouteNodeNavigationDomainModel]s that represents stops of a route
 * and a transport mode [String] that is used for showing
 * the duration and distance attributes of both the individual nodes
 * both the sum of all durations and distances for walking OR driving
 *
 * The first element of the [routeNodes] is always the stop generated from the start [com.example.model.Place]
 * of the current [com.example.model.Search]
 */
data class RouteNavigationDomainModel(
    val startPlace: PlaceNavigationDomainModel? = null,
    val routeNodes: List<RouteNodeNavigationDomainModel> = emptyList(),
    val transportMode: String = "foot-walking"
): Serializable {

    /*val fullWalkDuration: Int get() = routeNodes.sumOf { it.walkDuration }
    val fullWalkDistance: Int get() = routeNodes.sumOf { it.walkDistance }

    val fullCarDuration: Int get() = routeNodes.sumOf { it.carDuration }
    val fullCarDistance: Int get() = routeNodes.sumOf { it.carDistance }

    private var head: RouteNodeNavigationDomainModel? = null

    fun setStartPlace(startPlace: PlaceNavigationDomainModel){
        this.startPlace = startPlace
    }
    fun getStartPlace(): PlaceNavigationDomainModel?{
        return this.startPlace
    }

    fun setRouteNodes(routeNodes: ArrayList<RouteNodeNavigationDomainModel>): RouteNavigationDomainModel{
        return this.copy(routeNodes = routeNodes)
    }
    fun getRouteNodes(): List<RouteNodeNavigationDomainModel> {
        return this.routeNodes
    }
    fun addRouteNode(routeNode: RouteNodeNavigationDomainModel): RouteNavigationDomainModel{
        return this.copy(routeNodes = this.routeNodes.plus(routeNode))
    }

    fun getNodeByUUID(uuid: String): RouteNodeNavigationDomainModel? {

        return this.routeNodes.find { it.placeUUID == uuid }
    }

    fun getLeftNeighborOfNode(node: RouteNodeNavigationDomainModel): RouteNodeNavigationDomainModel {

        return this.routeNodes[this.routeNodes.indexOf(node)-1]
    }

    fun getRightNeighborOfNode(node: RouteNodeNavigationDomainModel): RouteNodeNavigationDomainModel? {

        if (this.routeNodes.indexOf(node) != this.routeNodes.size-1)
            return this.routeNodes[this.routeNodes.indexOf(node)+1]

        return null
    }

    fun removeRouteNodeByUUID(uuid: String): RouteNavigationDomainModel{
        val node = getNodeByUUID(
            uuid = uuid
        )
        if (node != null)
            return this.copy(routeNodes = this.routeNodes.minus(node))
        return this
    }

    *//*fun updateRouteByPosition(route: Route, index1: Int, index2: Int): Route {



    }
*//*
    *//** [updateRouteByUUID]
     * update a [RouteNodeNavigationDomainModel]'s [org.osmdroid.views.overlay.Polyline]'s
     * with new ones
     * Parameters: [uuid] of the node to be updated, and [newNode]
     * which has the new [org.osmdroid.views.overlay.Polyline]s
     *//*
    fun updateRouteByUUID(uuid: String, newNode: RouteNodeNavigationDomainModel): RouteNavigationDomainModel {

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

    *//** [move]
     * move a [RouteNodeNavigationDomainModel] to an other position
     * specified in the parameters [position], [node]
     *//*
    fun move(position: Int,node: RouteNodeNavigationDomainModel): RouteNavigationDomainModel {

        val mutableList = this.routeNodes.toMutableList()

        mutableList.remove(node)

        mutableList.add(position, node)

        return this.copy(

            routeNodes = mutableList.toList()
        )
    }

    *//** [replace]
     *  replace a [RouteNodeNavigationDomainModel] with an other [RouteNodeNavigationDomainModel]
     *  in the given [List] of [RouteNodeNavigationDomainModel]s
     *
     *  Parameters: the [List], the target [RouteNodeNavigationDomainModel]
     *  and the replacement [RouteNodeNavigationDomainModel]
     *//*
    private fun replace(list: List<RouteNodeNavigationDomainModel>, target: RouteNodeNavigationDomainModel, replacement: RouteNodeNavigationDomainModel): List<RouteNodeNavigationDomainModel> {

        val mutableList = list.toMutableList()

        val index = mutableList.indexOf(target)

        mutableList.remove(target)

        mutableList.add(index,replacement)

        return mutableList.toList()
    }

    *//** [getLastRouteNode]
     * return the last [RouteNodeNavigationDomainModel] in the list
     *//*
    fun getLastRouteNode(): RouteNodeNavigationDomainModel? {

        return this.routeNodes.last()
    }

    fun setTransportMode(transportMode: String): RouteNavigationDomainModel{
        return this.copy(transportMode = transportMode)
    }
    fun getTransportMode(): String{
        return this.transportMode
    }

    *//** [resetRoute]
     * returns the copy of the [RouteNavigationDomainModel]
     * but delete every other [RouteNodeNavigationDomainModel] than the first
     *//*
    fun resetRoute(): RouteNavigationDomainModel {
        return this.copy(
            routeNodes = listOf(this.routeNodes.first()),
            transportMode = "foot-walking"
        )
    }*/


    /*fun getRouteHead(): RouteNode? {

        return this.head
    }

    fun setRouteHead(routeNode: RouteNode?) {

        this.head = routeNode
    }*/

    /*fun getLastNode(): RouteNode? {

        if (this.head == null) return null

        return this.head?.prev
    }

    fun insertNode(routeNode: RouteNode) {

        if (this.head == null) {

            routeNode.next = routeNode
            routeNode.prev = routeNode
            this.head = routeNode
        } else {
            val last = this.head!!.prev

            last!!.next = routeNode
            routeNode.prev = last

            routeNode.next = this.head
            this.head!!.prev = routeNode
        }
    }

    fun deleteNodeByUUID(uuid: String): RouteNode? {

        val toBeDeleted = findNodeByUUID(
            uuid = uuid
        )
        if (toBeDeleted != null) {

            if (toBeDeleted == this.head) {

                if (this.head?.next == this.head) {

                    this.head = null
                } else {

                    this.head = toBeDeleted.next

                    this.head?.prev = toBeDeleted.prev

                    toBeDeleted.prev?.next = this.head
                }
            } else {

                toBeDeleted.next?.prev = toBeDeleted.prev

                toBeDeleted.prev?.next = toBeDeleted.next
            }
        }

        return toBeDeleted
    }

    fun findNodeByUUID(uuid: String): RouteNode? {
        var current = this.head

        if (current == null) return null // Empty list check

        do {
            if (current?.placeUUID == uuid) {
                return current
            }
            current = current?.next
        } while (current != this.head) // Stop when we've looped back

        return null // UUID not found
    }*/
}
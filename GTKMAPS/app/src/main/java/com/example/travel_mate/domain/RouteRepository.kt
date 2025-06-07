package com.example.travel_mate.domain

import com.example.travel_mate.data.Place
import com.example.travel_mate.data.RouteNode
import com.example.travel_mate.data.RouteRepositoryImpl.RouteState
import kotlinx.coroutines.flow.StateFlow

interface RouteRepository {

    val routeState: StateFlow<RouteState>

    fun getCurrentRouteNodes(): List<RouteNode>

    suspend fun setRouteTransportMode(index: Int)

    suspend fun initNewRoute(startPlace: Place)

    suspend fun resetRoute(all: Boolean)

    suspend fun addRemovePlaceToRoute(place: Place)

    suspend fun reorderRoute(newPosition: Int, nodeToMove: RouteNode)

    suspend fun removeStopFromRoute(uuid: String)

    suspend fun optimizeRoute()
}
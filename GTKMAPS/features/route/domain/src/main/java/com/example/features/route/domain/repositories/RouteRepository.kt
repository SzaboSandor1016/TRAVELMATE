package com.example.features.route.domain.repositories

import com.example.features.route.domain.models.CoordinatesRouteDomainModel
import com.example.features.route.domain.models.PlaceRouteDomainModel
import com.example.features.route.domain.models.RouteStateRouteDomainModel
import com.example.features.route.domain.models.info.RouteInfoRouteDomainModel
import com.example.features.route.domain.models.mapdata.RouteMapDataRouteDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface RouteRepository {

    val routeState: StateFlow<RouteStateRouteDomainModel>

    fun getRouteInfo(): Flow<RouteInfoRouteDomainModel>

    fun getRouteMapData(): Flow<RouteMapDataRouteDomainModel>

    fun isPlaceContained(uuid: String): Flow<Boolean>

    suspend fun testSetRouteTransportMode(index: Int)

    suspend fun testResetRoute(all: Boolean)

    fun getCurrentRouteNodesCoordinates(): List<CoordinatesRouteDomainModel>

    suspend fun setRouteTransportMode(index: Int)

    fun getRouteTransportMode(): String

    suspend fun initNewRoute(startPlace: PlaceRouteDomainModel)

    suspend fun resetRoute(all: Boolean)

    suspend fun addRemovePlace(place: PlaceRouteDomainModel)

    suspend fun removePlaceFromRouteByUUID(placeUUID: String)

    suspend fun reorderRoute(newPosition: Int, placeUUID: String)

    suspend fun optimizeRoute()
}
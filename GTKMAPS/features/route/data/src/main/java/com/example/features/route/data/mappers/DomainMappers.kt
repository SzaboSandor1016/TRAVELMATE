package com.example.features.route.data.mappers

import com.example.features.route.domain.models.RouteNodeRouteDomainModel
import com.example.features.route.domain.models.RouteStateRouteDomainModel
import com.example.features.route.domain.models.info.RouteInfoNodeRouteDomainModel
import com.example.features.route.domain.models.info.RouteInfoRouteDomainModel
import com.example.features.route.domain.models.mapdata.RouteMapDataRouteDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import org.osmdroid.views.overlay.Polyline

fun StateFlow<RouteStateRouteDomainModel>.toFlowOfRouteInfo(): Flow<RouteInfoRouteDomainModel> {
    return this.map { routeState ->
        RouteInfoRouteDomainModel(
            infoNodes = routeState.route.routeNodes.map { it.toRouteInfoNode(routeState.route.transportMode) },
            transportMode = routeState.route.transportMode,
            fullWalkDuration = routeState.route.fullWalkDuration,
            fullCarDuration = routeState.route.fullCarDuration,
            fullWalkDistance = routeState.route.fullWalkDistance,
            fullCarDistance = routeState.route.fullCarDistance
        )
    }
}

fun RouteNodeRouteDomainModel.toRouteInfoNode(transportMode: String): RouteInfoNodeRouteDomainModel {

    val duration = if(transportMode == "foot-walking") this.walkDuration else this.carDuration
    val distance = if(transportMode == "foot-walking") this.walkDistance else this.carDistance

    return RouteInfoNodeRouteDomainModel(
        placeUUID = this.placeUUID,
        name = this.name,
        coordinates = this.coordinate,
        duration = duration,
        distance = distance,
    )
}

fun StateFlow<RouteStateRouteDomainModel>.toFlowOfRouteMapData(): Flow<RouteMapDataRouteDomainModel> {
    return this.map { routeState ->

        val polylines: List<Polyline> = when(routeState.route.transportMode) {
            "driving-car" -> routeState.route.routeNodes.map { it.carPolyLine }
            else -> routeState.route.routeNodes.map { it.walkPolyLine }
        }

        RouteMapDataRouteDomainModel(
            polylines = polylines
        )
    }
}
package com.example.features.route.domain.models

import org.osmdroid.views.overlay.Polyline

data class RouteNodeRouteDomainModel(
    var walkPolyLine: Polyline,
    var carPolyLine: Polyline,
    var coordinate: CoordinatesRouteDomainModel,
    var name: String? = null,
    var matrixIndex: Int = 0, // needed for optimizing the route
    var approxDist: Double = 0.0, //the distance between this and the previous node calculated with haversine method
    var walkDistance: Int = 0,
    var walkDuration: Int = 0,
    var carDistance: Int = 0,
    var carDuration: Int = 0,
    var placeUUID: String,
    var walkRouteSteps: List<RouteStepRouteDomainModel> = emptyList(),
    var carRouteSteps: List<RouteStepRouteDomainModel> = emptyList()
) {

}
package com.example.features.navigation.domain.models

import org.osmdroid.views.overlay.Polyline

data class RouteNodeNavigationDomainModel(
    var walkPolyLine: Polyline,
    var carPolyLine: Polyline,
    var coordinate: CoordinatesNavigationDomainModel,
    var walkRouteSteps: List<RouteStepNavigationDomainModel> = emptyList(),
    var carRouteSteps: List<RouteStepNavigationDomainModel> = emptyList()
) {

    /*
        var prev: RouteNode? = null,
        var next: RouteNode? = null,*/

}
package com.example.travel_mate.data

import com.example.travel_mate.data.Coordinates
import com.example.travel_mate.data.RouteStep
import org.osmdroid.views.overlay.Polyline

data class RouteNode(var walkPolyLine: Polyline? = null,
                     var carPolyLine: Polyline? = null,
                     var coordinate: Coordinates? = null,
                     var name: String? = null,
                     var matrixIndex: Int = 0, // needed for optimizing the route
                     var approxDist: Double = 0.0, //the distance between this and the previous node calculated with harvesine method
                     var walkDistance: Int = 0,
                     var walkDuration: Int = 0,
                     var carDistance: Int = 0,
                     var carDuration: Int = 0,
                     var placeUUID: String? = null,
                     var walkRouteSteps: List<RouteStep> = emptyList(),
                     var carRouteSteps: List<RouteStep> = emptyList()
) {

    /*
        var prev: RouteNode? = null,
        var next: RouteNode? = null,*/

}
package com.example.travel_mate.data

import com.example.travel_mate.data.Coordinates

data class RouteStep (
    var instruction: String? = null,
    var name: String? = null,
    var distance: Int? = null,
    var duration: Int? = null,
    var type: Int? = null,
    var coordinates: Coordinates = Coordinates()
) {
}
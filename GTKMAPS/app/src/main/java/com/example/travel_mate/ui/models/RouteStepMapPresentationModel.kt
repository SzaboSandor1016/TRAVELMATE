package com.example.travel_mate.ui.models

data class RouteStepMapPresentationModel(
    var instruction: String? = null,
    var name: String? = null,
    var distance: Int? = null,
    var duration: Int? = null,
    var type: Int? = null,
    var coordinates: CoordinatesMapPresentationModel
) {
}
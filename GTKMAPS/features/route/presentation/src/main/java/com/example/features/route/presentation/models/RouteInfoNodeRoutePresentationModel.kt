package com.example.features.route.presentation.models

data class RouteInfoNodeRoutePresentationModel(
    val placeUUID: String,
    val name: String?,
    val coordinates: CoordinatesRoutePresentationModel,
    val duration: Int,
    val distance: Int
) {
}
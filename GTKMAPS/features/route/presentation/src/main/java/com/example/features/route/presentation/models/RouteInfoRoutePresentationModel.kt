package com.example.features.route.presentation.models

data class RouteInfoRoutePresentationModel(
    val infoNodes: List<RouteInfoNodeRoutePresentationModel> = emptyList(),
    val transportMode: String,
    val fullWalkDuration: Int,
    val fullCarDuration: Int,
    val fullWalkDistance: Int,
    val fullCarDistance: Int
) {
}
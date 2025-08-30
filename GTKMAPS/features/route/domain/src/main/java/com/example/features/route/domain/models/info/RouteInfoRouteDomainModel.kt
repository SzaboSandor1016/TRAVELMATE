package com.example.features.route.domain.models.info

data class RouteInfoRouteDomainModel(
    val infoNodes: List<RouteInfoNodeRouteDomainModel> = emptyList(),
    val transportMode: String,
    val fullWalkDuration: Int,
    val fullCarDuration: Int,
    val fullWalkDistance: Int,
    val fullCarDistance: Int
) {
}
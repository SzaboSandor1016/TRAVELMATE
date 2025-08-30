package com.example.features.route.domain.models.info


import com.example.features.route.domain.models.CoordinatesRouteDomainModel

data class RouteInfoNodeRouteDomainModel(
    val placeUUID: String,
    val name: String?,
    val coordinates: CoordinatesRouteDomainModel,
    val duration: Int,
    val distance: Int
) {
}
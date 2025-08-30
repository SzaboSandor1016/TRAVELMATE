package com.example.features.route.domain.models

class CoordinatesRouteDomainModel(
    val latitude: Double,
    val longitude: Double,
) {
    constructor(): this(
        latitude = 0.0,
        longitude = 0.0
    )
}
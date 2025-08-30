package com.example.features.navigation.domain.models

class CoordinatesNavigationDomainModel(
    val latitude: Double,
    val longitude: Double,
) {
    constructor(): this(
        latitude = 0.0,
        longitude = 0.0
    )
}
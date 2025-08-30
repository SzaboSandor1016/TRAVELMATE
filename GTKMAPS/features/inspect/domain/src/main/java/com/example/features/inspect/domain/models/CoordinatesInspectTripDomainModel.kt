package com.example.features.inspect.domain.models

class CoordinatesInspectTripDomainModel(
    val latitude: Double,
    val longitude: Double,
) {
    constructor(): this(
        latitude = 0.0,
        longitude = 0.0
    )
}
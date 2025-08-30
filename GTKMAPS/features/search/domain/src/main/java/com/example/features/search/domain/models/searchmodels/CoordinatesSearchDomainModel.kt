package com.example.features.search.domain.models.searchmodels

class CoordinatesSearchDomainModel(
    val latitude: Double,
    val longitude: Double,
) {
    constructor(): this(
        latitude = 0.0,
        longitude = 0.0
    )
}
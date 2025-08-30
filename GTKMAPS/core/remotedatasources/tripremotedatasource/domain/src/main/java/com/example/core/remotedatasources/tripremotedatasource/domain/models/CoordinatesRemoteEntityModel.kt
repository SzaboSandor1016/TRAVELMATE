package com.example.core.remotedatasources.tripremotedatasource.domain.models

data class CoordinatesRemoteEntityModel(
    val latitude: Double,
    val longitude: Double,
) {
    constructor(): this(
        0.0,
        0.0
    )
}
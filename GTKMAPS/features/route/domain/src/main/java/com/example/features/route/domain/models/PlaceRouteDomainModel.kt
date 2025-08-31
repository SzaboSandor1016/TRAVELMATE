package com.example.features.route.domain.models

import java.util.UUID

data class PlaceRouteDomainModel(
    val uUID: String,
    val name: String?,
    val coordinates: CoordinatesRouteDomainModel,
) {
    constructor(
        coordinates: CoordinatesRouteDomainModel
    ): this(
        uUID = UUID.randomUUID().toString(),
        name = "",
        coordinates = coordinates,
    )

}
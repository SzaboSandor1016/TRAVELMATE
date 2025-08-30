package com.example.features.navigation.domain.models

import java.util.UUID

class PlaceNavigationDomainModel(
    var uUID: String,
    var name: String,
    var cuisine: String?,
    var openingHours: String?,
    var charge: String?,
    var address: AddressNavigationDomainModel,
    var coordinates: CoordinatesNavigationDomainModel,
    var category: String,
) {
    constructor(
        address: AddressNavigationDomainModel,
        coordinates: CoordinatesNavigationDomainModel
    ): this(
        uUID = UUID.randomUUID().toString(),
        name = "",
        cuisine = "",
        openingHours = "",
        charge = "",
        address = address,
        coordinates = coordinates,
        category = ""
    )
}
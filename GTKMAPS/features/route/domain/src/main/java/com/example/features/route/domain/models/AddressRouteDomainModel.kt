package com.example.features.route.domain.models

class AddressRouteDomainModel(
    val city: String?,
    val street: String?,
    val houseNumber: String?,
    val country: String?
) {
    constructor(): this(
        city = "",
        street = "",
        houseNumber = "",
        country = ""
    )
}
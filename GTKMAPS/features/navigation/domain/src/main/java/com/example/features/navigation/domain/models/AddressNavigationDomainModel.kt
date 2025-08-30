package com.example.features.navigation.domain.models

class AddressNavigationDomainModel(
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
package com.example.features.search.domain.models.searchmodels

class AddressSearchDomainModel(
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
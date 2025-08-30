package com.example.core.remotedatasources.tripremotedatasource.domain.models

data class AddressRemoteEntityModel(
    val city: String?,
    val street: String?,
    val houseNumber: String?,
    val country: String?

) {

    constructor(): this(
        null,
        null,
        null,
        null
    )
}
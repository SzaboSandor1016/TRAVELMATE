package com.example.core.remotedatasources.tripremotedatasource.domain.models


data class PlaceRemoteEntityModel(
    var uUID: String,
    var name: String?,
    var cuisine: String?,
    var openingHours: String?,
    var charge: String?,
    var address: AddressRemoteEntityModel,
    var coordinates: CoordinatesRemoteEntityModel,
    var category: String,
    /*var containedByTrip: Boolean = false,
    var containedByRoute: Boolean = false,*/
) {
    constructor(): this(
        uUID = "",
        name = null,
        cuisine = null,
        openingHours = null,
        charge = null,
        address = AddressRemoteEntityModel(),
        coordinates = CoordinatesRemoteEntityModel(),
        category = ""
    )
}
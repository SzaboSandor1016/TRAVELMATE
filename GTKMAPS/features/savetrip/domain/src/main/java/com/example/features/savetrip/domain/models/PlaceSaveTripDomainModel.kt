package com.example.features.savetrip.domain.models


data class PlaceSaveTripDomainModel(
    val dayOfTripUUID: String? = null,
    var uUID: String,
    var name: String?,
    var cuisine: String?,
    var openingHours: String?,
    var charge: String?,
    var address: AddressSaveTripDomainModel,
    var coordinates: CoordinatesSaveTripDomainModel,
    var category: String,
    /*var containedByTrip: Boolean = false,
    var containedByRoute: Boolean = false,*/
) {
}
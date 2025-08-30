package com.example.features.trips.domain.models


data class PlaceTripsDomainModel(
    val dayOfTripUUID: String? = null,
    var uUID: String,
    var name: String?,
    var cuisine: String?,
    var openingHours: String?,
    var charge: String?,
    var address: AddressTripsDomainModel,
    var coordinates: CoordinatesTripsDomainModel,
    var category: String,
    /*var containedByTrip: Boolean = false,
    var containedByRoute: Boolean = false,*/
) {
}
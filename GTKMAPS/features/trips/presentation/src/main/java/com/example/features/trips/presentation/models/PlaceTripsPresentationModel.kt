package com.example.features.trips.presentation.models


data class PlaceTripsPresentationModel(
    var uUID: String,
    var name: String,
    var cuisine: String?,
    var openingHours: String?,
    var charge: String?,
    var address: AddressTripsPresentationModel,
    var coordinates: CoordinatesTripsPresentationModel,
    var category: String,
    /*var containedByTrip: Boolean = false,
    var containedByRoute: Boolean = false,*/
) {
}
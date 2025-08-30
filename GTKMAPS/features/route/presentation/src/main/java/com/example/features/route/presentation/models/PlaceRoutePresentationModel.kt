package com.example.features.route.presentation.models

data class PlaceRoutePresentationModel(
    val uUID: String,
    val name: String?,
    val cuisine: String?,
    val openingHours: String?,
    val charge: String?,
    val address: AddressRoutePresentationModel,
    val coordinates: CoordinatesRoutePresentationModel,
    val category: String,
    /*    private val containedByTrip: Boolean = false,
        private val containedByRoute: Boolean = false*/
) {

}
package com.example.travel_mate.ui.models

data class PlaceFullMapPresentationModel(
    val uUID: String,
    val name: String?,
    val cuisine: String?,
    val openingHours: String?,
    val charge: String?,
    val address: AddressMapPresentationModel,
    val coordinates: CoordinatesMapPresentationModel,
    val category: String
) {
}
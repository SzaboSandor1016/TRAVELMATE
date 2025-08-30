package com.example.features.findcustom.presentation.models

data class PlaceCustomPlacePresentationModel(
    val uUID: String?,
    val name: String?,
    val address: AddressCustomPlacePresentationModel,
    val coordinates: CoordinatesCustomPlacePresentationModel
) {
}
package com.example.features.search.presentation.models

data class PlaceSearchPresentationModel(
    val uuid: String,
    val name: String?,
    val address: AddressSearchPresentationModel,
    val coordinates: CoordinatesSearchPresentationModel,
    val category: String
) {
}
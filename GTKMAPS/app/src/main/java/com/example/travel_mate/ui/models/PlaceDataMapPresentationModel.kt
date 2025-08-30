package com.example.travel_mate.ui.models

data class PlaceDataMapPresentationModel(
    val uuid: String,
    val name: String?,
    val coordinates: CoordinatesMapPresentationModel,
    val category: String
) {
}
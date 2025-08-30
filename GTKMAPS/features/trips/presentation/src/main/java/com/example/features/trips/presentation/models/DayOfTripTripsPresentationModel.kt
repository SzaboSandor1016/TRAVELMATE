package com.example.features.trips.presentation.models

data class DayOfTripTripsPresentationModel(
    val dayUUID: String? = null,
    val tripUUID: String? = null,
    val label: String,
    val places: List<PlaceTripsPresentationModel>
) {
}
package com.example.features.inspect.presentation.models

data class DayOfTripInfoInspectTripPresentationModel(
    val selected: Boolean,
    val label: String,
    val placesInfo: List<PlaceInfoInspectTripPresentationModel>
) {
}
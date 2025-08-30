package com.example.features.inspect.domain.models.info

data class DayOfTripInfoInspectTripDomainModel(
    val selected: Boolean,
    val label: String,
    val placesInfo: List<PlaceInfoInspectTripDomainModel>
) {
}
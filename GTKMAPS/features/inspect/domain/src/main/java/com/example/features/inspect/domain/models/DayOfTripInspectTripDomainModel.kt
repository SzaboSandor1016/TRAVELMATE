package com.example.features.inspect.domain.models

data class DayOfTripInspectTripDomainModel(
    val label: String,
    val places: List<PlaceInspectTripDomainModel> = emptyList()
) {
}
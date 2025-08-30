package com.example.features.trips.domain.models

data class DayOfTripTripsDomainModel(
    val dayUUID: String? = null,
    val tripUUID: String? = null,
    val label: String,
    val places: List<PlaceTripsDomainModel>,
) {
}
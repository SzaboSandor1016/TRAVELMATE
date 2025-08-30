package com.example.features.savetrip.domain.models

data class DayOfTripSaveTripDomainModel(
    val dayUUID: String? = null,
    val tripUUID: String? = null,
    val label: String,
    val places: List<PlaceSaveTripDomainModel> = emptyList()
) {
}
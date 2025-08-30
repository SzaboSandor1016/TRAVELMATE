package com.example.features.savetrip.domain.models

data class SaveTripInfoSaveTripDomainModel(
    val date: String?,
    val title: String?,
    val note: String?,
    val contributorUsernames: List<String>,
    val daysOfTrip: List<DayOfTripInfoSaveTripDomainModel>,
) {
}
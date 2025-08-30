package com.example.features.savetrip.presentation.models

import com.example.features.savetrip.domain.models.DayOfTripInfoSaveTripDomainModel

data class SaveTripInfoPresentationModel(
    val date: String? = null,
    val title: String? = null,
    val note: String? = null,
    val contributorUsernames: List<String> = emptyList(),
    val daysOfTrip: List<DayOfTripSaveTripPresentationModel> = emptyList()
) {
}
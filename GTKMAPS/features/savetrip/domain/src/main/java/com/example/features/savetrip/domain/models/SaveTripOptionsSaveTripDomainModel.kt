package com.example.features.savetrip.domain.models

data class SaveTripOptionsSaveTripDomainModel(
    val selectedDayPosition: Int = -1,
    val originalDays: List<DayOfTripSaveTripDomainModel> = emptyList()
) {
}
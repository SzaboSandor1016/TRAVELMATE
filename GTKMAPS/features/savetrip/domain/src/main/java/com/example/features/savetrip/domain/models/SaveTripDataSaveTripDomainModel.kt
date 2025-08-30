package com.example.features.savetrip.domain.models

import java.util.UUID

data class SaveTripDataSaveTripDomainModel(
    val uUID: String = UUID.randomUUID().toString(),
    val startPlace: PlaceSaveTripDomainModel? = null,
    val daysOfTrip: List<DayOfTripSaveTripDomainModel> = emptyList(),
    val creatorUID: String? = null,
    val selectedContributorsUIDs: Map<String, Boolean> = hashMapOf(),
    val selectedContributorsParameters: Map<String, ContributorSaveTripDomainModel> = emptyMap(),
    val numberOfDays: Int = 0,
    val date: String? = null,
    val title: String? = null,
    val note: String? = null,
) {
}
package com.example.features.savetrip.domain.models

data class SaveTripStateSaveTripDomainModel(
    val saveTripData: SaveTripDataSaveTripDomainModel = SaveTripDataSaveTripDomainModel(),
    val saveTripOptions: SaveTripOptionsSaveTripDomainModel = SaveTripOptionsSaveTripDomainModel()
) {
}
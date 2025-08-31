package com.example.features.savetrip.domain.usecases

import com.example.features.user.domain.repositories.UserRepository
import com.example.features.savetrip.domain.models.PlaceSaveTripDomainModel
import com.example.features.savetrip.domain.repository.SaveTripRepository

class InitSaveFromSearch(
    private val saveTripRepository: SaveTripRepository,
) {

    suspend operator fun invoke(startPlace: PlaceSaveTripDomainModel) {

        saveTripRepository.initSaveFromSearchWithStartPlace(
            startPlace = startPlace
        )
    }
}
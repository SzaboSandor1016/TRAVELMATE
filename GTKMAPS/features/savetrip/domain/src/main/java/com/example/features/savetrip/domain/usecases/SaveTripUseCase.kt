package com.example.features.savetrip.domain.usecases

import com.example.features.savetrip.domain.repository.SaveTripRepository

class SaveTripUseCase(
    private val saveTripRepository: SaveTripRepository
) {
    suspend operator fun invoke() {

        val areContributorsEmpty = saveTripRepository.areContributorsEmpty()

        if (areContributorsEmpty) {
            saveTripRepository.uploadTripToLocalDatabase()
        } else {
            saveTripRepository.uploadTripToRemoteDatabase()
        }
    }
}
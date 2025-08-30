package com.example.features.savetrip.domain.usecases

import com.example.features.savetrip.domain.repository.SaveTripRepository

class RemoveDayFromTripUseCase(
    private val saveTripRepository: SaveTripRepository
) {

    suspend operator fun invoke(index: Int) {

        saveTripRepository.removeDayFromTrip(
            index = index
        )
    }
}
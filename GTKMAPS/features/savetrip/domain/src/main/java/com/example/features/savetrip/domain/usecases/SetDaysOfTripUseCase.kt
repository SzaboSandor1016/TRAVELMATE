package com.example.features.savetrip.domain.usecases

import com.example.features.savetrip.domain.repository.SaveTripRepository

class SetDaysOfTripUseCase(
    private val saveTripRepository: SaveTripRepository
) {

    suspend operator fun invoke(daysLabels: List<String>) {

        saveTripRepository.setDaysOfTrip(
            daysLabels = daysLabels
        )
    }
}
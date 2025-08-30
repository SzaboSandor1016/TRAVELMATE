package com.example.features.savetrip.domain.usecases

import com.example.features.savetrip.domain.repository.SaveTripRepository

class SetSaveTripDateUseCase(
    private val saveTripRepository: SaveTripRepository
) {

    suspend operator fun invoke(date: String) {

        saveTripRepository.setTripDate(
            date = date
        )
    }
}
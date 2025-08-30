package com.example.features.savetrip.domain.usecases

import com.example.features.savetrip.domain.repository.SaveTripRepository

class SetSelectedDayOfSaveTripUseCase(
    private val saveTripRepository: SaveTripRepository
) {

    suspend operator fun invoke(index: Int)  {

        saveTripRepository.setSelectedDayWith(index)
    }
}
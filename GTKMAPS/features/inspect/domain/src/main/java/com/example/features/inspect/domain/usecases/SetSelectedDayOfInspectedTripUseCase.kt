package com.example.features.inspect.domain.usecases

import com.example.features.inspect.domain.repositories.CurrentTripRepository

class SetSelectedDayOfInspectedTripUseCase(
    private val currentTripRepository: CurrentTripRepository
) {

    suspend operator fun invoke(index: Int) {

        currentTripRepository.setSelectedInspectedDay(
            position = index
        )
    }
}
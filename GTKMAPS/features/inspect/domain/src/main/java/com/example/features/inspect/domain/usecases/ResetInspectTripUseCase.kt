package com.example.features.inspect.domain.usecases

import com.example.features.inspect.domain.repositories.CurrentTripRepository

class ResetInspectTripUseCase(
    private val currentTripRepository: CurrentTripRepository
) {

    operator fun invoke() {

        currentTripRepository.resetCurrentTrip()
    }
}
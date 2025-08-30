package com.example.features.selectedplace.domain.usecases

import com.example.features.selectedplace.domain.repository.SelectedPlaceRepository

class ResetSelectedPlaceUseCase(
    private val selectedPlaceRepository: SelectedPlaceRepository
) {

    operator fun invoke() {

        selectedPlaceRepository.resetSelectedPlace()
    }
}
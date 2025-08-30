package com.example.features.selectedplace.domain.usecases

import com.example.features.selectedplace.domain.models.SelectedPlaceSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.repository.SelectedPlaceRepository

class SetSelectedPlaceUseCase(
    private val selectedPlaceRepository: SelectedPlaceRepository
) {
    operator fun invoke(selectedPlace: SelectedPlaceSelectedPlaceDomainModel.Selected) {

        selectedPlaceRepository.setSelectedPlace(
            selectedPlace = selectedPlace
        )
    }
}
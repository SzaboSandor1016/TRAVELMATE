package com.example.features.selectedplace.domain.usecases

import com.example.features.selectedplace.domain.models.SelectedPlaceSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.repository.SelectedPlaceRepository

class GetSelectedPlaceFullDataUseCase(
    private val selectedPlaceRepository: SelectedPlaceRepository
) {

    operator fun invoke(): SelectedPlaceSelectedPlaceDomainModel.Selected {

        return selectedPlaceRepository.getFullSelectedPlaceData()
    }
}
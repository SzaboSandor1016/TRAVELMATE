package com.example.features.selectedplace.domain.usecases

import com.example.features.selectedplace.domain.models.SelectedPlaceInfoSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.repository.SelectedPlaceRepository
import kotlinx.coroutines.flow.Flow

class GetSelectedPlaceInfoUseCase(
    private val selectedPlaceRepository: SelectedPlaceRepository
) {

    operator fun invoke(): Flow<SelectedPlaceInfoSelectedPlaceDomainModel> {

        return selectedPlaceRepository.getSelectedPlaceInfo()
    }
}
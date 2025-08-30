package com.example.features.selectedplaceoptions.domain.usecases

import com.example.features.selectedplaceoptions.domain.models.SelectedPlaceSelectedPlaceOptions
import com.example.features.selectedplaceoptions.domain.repository.SelectedPlaceOptionsRepository
import kotlinx.coroutines.flow.Flow

class GetSelectedPlaceSelectedPlaceOptionsAsFlowUseCase(
    private val selectedPlaceOptionsRepository: SelectedPlaceOptionsRepository
) {

    operator fun invoke(): Flow<SelectedPlaceSelectedPlaceOptions> {

        return selectedPlaceOptionsRepository.getSelectedPlaceOptionsAsFlow()
    }
}
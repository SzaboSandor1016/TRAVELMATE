package com.example.features.selectedplaceoptions.domain.usecases

import com.example.features.selectedplaceoptions.domain.models.MainSelectedPlaceOptions
import com.example.features.selectedplaceoptions.domain.repository.SelectedPlaceOptionsRepository
import kotlinx.coroutines.flow.Flow

class GetMainSelectedPlaceOptionsUseCase(
    private val selectedPlaceOptionsRepository: SelectedPlaceOptionsRepository
) {

    operator fun invoke(): Flow<MainSelectedPlaceOptions> {

        return selectedPlaceOptionsRepository.getMainOptionsAsFlow()
    }
}
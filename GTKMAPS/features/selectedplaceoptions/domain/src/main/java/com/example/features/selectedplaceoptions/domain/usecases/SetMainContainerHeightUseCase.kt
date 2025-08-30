package com.example.features.selectedplaceoptions.domain.usecases

import com.example.features.selectedplaceoptions.domain.repository.SelectedPlaceOptionsRepository

class SetMainContainerHeightUseCase(
    private val selectedPlaceOptionsRepository: SelectedPlaceOptionsRepository
) {

    operator fun invoke(height: Int) {

        selectedPlaceOptionsRepository.setMainContainerHeight(
            height = height
        )
    }
}
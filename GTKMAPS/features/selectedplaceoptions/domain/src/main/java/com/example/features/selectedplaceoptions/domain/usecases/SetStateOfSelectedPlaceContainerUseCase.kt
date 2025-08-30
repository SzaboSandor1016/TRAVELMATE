package com.example.features.selectedplaceoptions.domain.usecases

import com.example.features.selectedplaceoptions.domain.repository.SelectedPlaceOptionsRepository

class SetStateOfSelectedPlaceContainerUseCase(
    private val selectedPlaceOptionsRepository: SelectedPlaceOptionsRepository
) {

    operator fun invoke(containerState: String)  {

        selectedPlaceOptionsRepository.setStateOfSelectedPlaceContainer(
            state = containerState
        )
    }
}
package com.example.features.selectedplaceoptions.domain.usecases

import com.example.features.selectedplaceoptions.domain.repository.SelectedPlaceOptionsRepository

class SetOriginOfSelectedPlaceUseCase(
    private val selectedPlaceOptionsRepository: SelectedPlaceOptionsRepository
) {

    operator fun invoke(origin: String)  {

        selectedPlaceOptionsRepository.setOriginOfSelectedPlace(
            origin = origin
        )
    }
}
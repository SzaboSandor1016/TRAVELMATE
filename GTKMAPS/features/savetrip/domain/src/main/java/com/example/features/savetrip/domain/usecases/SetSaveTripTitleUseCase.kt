package com.example.features.savetrip.domain.usecases

import com.example.features.savetrip.domain.repository.SaveTripRepository

class SetSaveTripTitleUseCase(
    private val saveTripRepository: SaveTripRepository
) {

    suspend operator fun invoke(title: String) {

        saveTripRepository.setTripTitle(
            title = title
        )
    }
}
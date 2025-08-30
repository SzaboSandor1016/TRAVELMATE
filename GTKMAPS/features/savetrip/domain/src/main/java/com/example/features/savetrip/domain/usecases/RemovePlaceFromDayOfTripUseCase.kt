package com.example.features.savetrip.domain.usecases

import com.example.features.savetrip.domain.repository.SaveTripRepository

class RemovePlaceFromDayOfTripUseCase(
    private val saveTripRepository: SaveTripRepository
) {

    suspend operator fun invoke(
        placeUUID: String
    ) {
        saveTripRepository.removePlaceFromDayOfTrip(
            placeUUID = placeUUID
        )
    }
}
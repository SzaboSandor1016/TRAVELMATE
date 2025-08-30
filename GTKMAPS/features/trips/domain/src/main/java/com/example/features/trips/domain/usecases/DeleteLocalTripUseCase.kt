package com.example.features.trips.domain.usecases

import com.example.features.trips.domain.repositories.TripRepository


class DeleteLocalTripUseCase(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(tripUUID: String) {

        tripRepository.deleteCurrentTripFromLocalDatabase(
            tripUID = tripUUID
        )
    }
}
package com.example.travel_mate.domain

import com.example.travel_mate.data.Trip

class DeleteLocalTripUseCase(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(trip: Trip) {

        tripRepository.deleteCurrentTripFromLocalDatabase(
            trip = trip
        )
    }
}
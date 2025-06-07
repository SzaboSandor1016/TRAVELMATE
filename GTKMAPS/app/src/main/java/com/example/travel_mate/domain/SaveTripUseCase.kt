package com.example.travel_mate.domain

import com.example.travel_mate.data.Trip
import com.example.travel_mate.data.TripRepositoryImpl.TripIdentifier

class SaveTripUseCase(
    private val saveLocalTripUseCase: SaveLocalTripUseCase,
    private val saveRemoteTripUseCase: SaveRemoteTripUseCase,
) {
    suspend operator fun invoke(trip: Trip, tripIdentifier: TripIdentifier) {

        when (tripIdentifier.contributors.isEmpty()) {

            true -> {

                saveLocalTripUseCase(
                    trip = trip,
                    tripIdentifier = tripIdentifier
                )
            }

            false -> {

                saveRemoteTripUseCase(
                    trip = trip,
                    tripIdentifier = tripIdentifier
                )
            }
        }
    }
}
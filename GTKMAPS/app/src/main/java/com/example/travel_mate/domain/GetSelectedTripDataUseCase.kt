package com.example.travel_mate.domain

import com.example.travel_mate.data.Trip
import com.example.travel_mate.data.TripRepositoryImpl.TripIdentifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetSelectedTripDataUseCase(
    private val tripRepository: TripRepository,
    private val currentTripRepository: CurrentTripRepository
) {

    suspend operator fun invoke(tripIdentifier: TripIdentifier) {

        val trip = when(tripIdentifier.location) {

            "local" -> tripRepository.getCurrentLocalTripData(tripIdentifier)
            else -> tripRepository.getCurrentRemoteTripData(tripIdentifier)
        }

        trip.collect {

            currentTripRepository.setCurrentTrip(
                trip = it,
                tripIdentifier = tripIdentifier
            )
        }
    }
}
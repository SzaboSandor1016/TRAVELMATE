package com.example.travel_mate.domain

import com.example.travel_mate.data.Place
import com.example.travel_mate.data.Trip
import com.example.travel_mate.data.TripRepositoryImpl.TripIdentifier

class SaveTripWithUpdatedPlacesUseCase(
    private val currentTripRepository: CurrentTripRepository,
    private val saveTripUseCase: SaveTripUseCase
) {
    suspend operator fun invoke(startPlace: Place, places: List<Place>) {

        val current = currentTripRepository.getCurrentTrip()

        val trip = current.first ?: Trip()

        val identifier = current.second ?: TripIdentifier()

        saveTripUseCase(
            trip = trip.copy(
                startPlace = startPlace,
                places = places
            ),
            tripIdentifier = identifier
        )
    }
}
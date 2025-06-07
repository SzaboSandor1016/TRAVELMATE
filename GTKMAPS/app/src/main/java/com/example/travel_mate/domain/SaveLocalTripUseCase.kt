package com.example.travel_mate.domain

import com.example.travel_mate.data.Trip
import com.example.travel_mate.data.TripRepositoryImpl

class SaveLocalTripUseCase(
    private val tripRepository: TripRepository,
    private val deleteRemoteTripUseCase: DeleteRemoteTripUseCase
){

    suspend operator fun invoke(
        trip: Trip,
        tripIdentifier: TripRepositoryImpl.TripIdentifier
    ) {
        tripRepository.uploadTripToLocalDatabase(
            trip = trip
        )

        //todo check where to delete here or in SaveTripUseCase
        if (tripIdentifier.location.equals("remote")) {
            deleteRemoteTripUseCase(
                tripIdentifier.creatorUID!!,
                tripUuid = trip.uUID
            )
        }
    }
}
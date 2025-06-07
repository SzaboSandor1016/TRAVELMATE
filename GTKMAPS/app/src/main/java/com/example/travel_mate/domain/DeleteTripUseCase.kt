package com.example.travel_mate.domain

import com.example.travel_mate.data.Trip
import com.example.travel_mate.data.TripRepositoryImpl.TripIdentifier

class DeleteTripUseCase(
    private val deleteLocalTripUseCase: DeleteLocalTripUseCase,
    private val deleteRemoteTripUseCase: DeleteRemoteTripUseCase
){
    suspend operator fun invoke(trip: Trip, tripIdentifier: TripIdentifier) {

        if (tripIdentifier.location == "local") {

            deleteLocalTripUseCase(trip = trip)
        } else {

            deleteRemoteTripUseCase(
                tripUuid = trip.uUID,
                creatorUid = tripIdentifier.creatorUID!!
            )
        }
    }
}
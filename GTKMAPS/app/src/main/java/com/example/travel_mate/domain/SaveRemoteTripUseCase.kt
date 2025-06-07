package com.example.travel_mate.domain

import com.example.travel_mate.data.Trip
import com.example.travel_mate.data.TripRepositoryImpl

class SaveRemoteTripUseCase(
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository,
    private val deleteLocalTripUseCase: DeleteLocalTripUseCase
) {
    suspend operator fun invoke(
        trip: Trip,
        tripIdentifier: TripRepositoryImpl.TripIdentifier
    ) {

        when (tripIdentifier.creatorUID) {

            null -> {

                val userUid = userRepository.getCurrentUserUid()

                tripRepository.uploadTripToRemoteDatabase(
                    userUid = userUid,
                    trip = trip,
                    tripIdentifier = tripIdentifier
                )
            }
            else -> tripRepository.uploadContributedTripToRemoteDatabase(
                trip = trip,
                tripIdentifier = tripIdentifier
            )
        }

//todo check where to delete here or in SaveTripUseCase
        if (tripIdentifier.location.equals("local")) {
            deleteLocalTripUseCase(
                trip = trip
            )
        }
    }
}
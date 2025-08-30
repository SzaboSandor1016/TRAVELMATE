package com.example.features.trips.domain.usecases

import com.example.core.auth.domain.FirebaseAuthenticationSource
import com.example.features.trips.domain.repositories.TripRepository

class DeleteRemoteTripUseCase(
    private val tripRepository: TripRepository
){

    suspend operator fun invoke(creatorUid: String, tripUuid: String) {

        tripRepository.getCurrentUserId().collect {

            if (it != null) {

                if (creatorUid == it) {

                    tripRepository.deleteCurrentTripFromRemoteDatabase(
                        tripUuid = tripUuid
                    )
                } else {

                    tripRepository.deleteUidFromContributedTrips(
                        uid = it, tripUuid = tripUuid
                    )
                }
            }
        }
    }
}
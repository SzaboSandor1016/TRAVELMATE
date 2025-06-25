package com.example.travel_mate.domain

class DeleteRemoteTripUseCase(
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository
){

    suspend operator fun invoke(creatorUid: String,tripUuid: String) {

        val userUid = userRepository.getCurrentUserUid()?: return

        if (creatorUid == userUid) {

            tripRepository.deleteCurrentTripFromRemoteDatabase(
                tripUuid = tripUuid
            )
        } else {

            tripRepository.deleteUidFromContributedTrips(
                uid = userUid,
                tripUuid = tripUuid
            )
        }
    }
}
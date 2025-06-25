package com.example.travel_mate.domain

class DeleteUserUseCase(
    private val userRepository: UserRepository,
    private val tripRepository: TripRepository
) {

    suspend operator fun invoke(password: String) {

        val userUid = userRepository.getCurrentUserUid()?: return

        tripRepository.deleteUserTripsFromRemoteDatabase(
            userUid = userUid
        )

        userRepository.deleteCurrentUser(
            userUid = userUid, password = password
        )
    }
}
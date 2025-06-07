package com.example.travel_mate.domain

class SetCurrentTripContributorsUseCase(
    private val currentTripRepository: CurrentTripRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {

        val currentContributors = userRepository.getCurrentContributors()

        currentTripRepository.setCurrentTripContributors(
            contributors = currentContributors
        )
    }
}
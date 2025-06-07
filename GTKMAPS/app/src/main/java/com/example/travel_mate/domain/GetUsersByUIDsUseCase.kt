package com.example.travel_mate.domain

class GetUsersByUIDsUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uIds: List<String>): Map<String, String> {

        return userRepository.getUsersByUIDs(
            uIds = uIds
        )
    }
}
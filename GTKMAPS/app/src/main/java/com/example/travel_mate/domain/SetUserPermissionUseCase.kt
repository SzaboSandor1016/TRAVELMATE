package com.example.travel_mate.domain

class SetUserPermissionUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(uid: String, canUpdate: Boolean) {

        val newContributors = userRepository.getCurrentContributors().toMutableMap()

        val toBeUpdated = newContributors.getValue(uid)

        newContributors.replace(
            uid,
            toBeUpdated.setPermissionToUpdate(canUpdate = canUpdate)
        )

        userRepository.setCurrentContributors(newContributors)
    }
}
package com.example.travel_mate.domain

import com.example.travel_mate.data.Contributor

class GetSelectableContributorsUseCase(
    private val userRepository: UserRepository,
    private val currentTripRepository: CurrentTripRepository,
    private val getUsersByUIDsUseCase: GetUsersByUIDsUseCase
) {
    suspend operator fun invoke() {

        val userUid = userRepository.getCurrentUserUid()

        val contributorsOfTrip = currentTripRepository.getCurrentTripContributors()

        val recentContributorsOfUser = userRepository.getRecentContributorsOfUser(userUid)

        val userPairs = getUsersByUIDsUseCase(recentContributorsOfUser)
            .map {

            Pair(
                it.key,
                Contributor(
                    uid = it.key,
                    username = it.value,
                    selected = false
                )
            )
        }.toMap()

        var fullContributors: MutableMap<String, Contributor> = mutableMapOf()

        fullContributors.putAll(contributorsOfTrip)

        fullContributors.putAll(userPairs.filter {

            !fullContributors.map { it.value.username.toString() }
                .contains(it.value.username.toString())
        })

        userRepository.setCurrentContributors(fullContributors)
    }
}
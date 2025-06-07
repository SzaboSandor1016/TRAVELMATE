package com.example.travel_mate.domain

import com.example.travel_mate.data.Contributor
import com.example.travel_mate.data.TripRepositoryImpl.TripIdentifier

class ProcessTripIdentifiersUseCase(
    private val userRepository: UserRepository,
    private val getUsersByUIDsUseCase: GetUsersByUIDsUseCase
) {

    suspend operator fun invoke(tripIdentifiers: List<TripIdentifier>): List<TripIdentifier> {

        val currentUser = userRepository.getCurrentUserUid()

        return tripIdentifiers.map { tripIdentifier -> processFetchedTrip(tripIdentifier, currentUser) }
    }

    //todo reminder this goes to a UseCase
    /** [processFetchedTrips]
     *  sets values for the uid, username and selected fields for each [TripIdentifier]s passed as parameter
     *
     *  @param tripIdentifiers the trip identifiers needed to be processed
     *
     *  @return the list of the processed trip identifiers
     */
    private suspend fun processFetchedTrip(tripIdentifier: TripIdentifier, userUid: String): TripIdentifier {

        val creatorUsername = getUserUsername(tripIdentifier.creatorUID.toString())

        val contributors = mapContributorsOfTrip(tripIdentifier.contributors)

        val permissionToUpdate = when (tripIdentifier.creatorUID == userUid) {

            false -> contributors.any { (it.key == userUid && it.value.canUpdate == true) }

            true -> true
        }

        val contributorUIDs = tripIdentifier.contributors.toMutableMap().mapValues {
            true
        }

        return tripIdentifier.copy(
            permissionToUpdate = permissionToUpdate,
            creatorUsername = creatorUsername,
            contributorUIDs = contributorUIDs,
            contributors = contributors
        )
    }

    private suspend fun mapContributorsOfTrip(contributors: Map<String, Contributor>): Map<String, Contributor> {

        return contributors.toMutableMap().mapValues {

            val username = getUserUsername(uid = it.key)

            it.value.copy(
                uid = it.key,
                username = username,
                selected = true
            )
        }
    }

    private suspend fun getUserUsername(uid: String): String {

        return getUsersByUIDsUseCase(listOf(uid)).map { it.value }[0]
    }
}
package com.example.travel_mate.domain

import com.example.travel_mate.data.TripRepositoryImpl.TripIdentifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class GetRemoteContributedTripsUseCase(
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository,
    private val processTripIdentifiersUseCase: ProcessTripIdentifiersUseCase
) {
    suspend operator fun invoke(): Flow<List<TripIdentifier>> {

        val currentUser = userRepository.getCurrentUserUid()?: return flowOf(emptyList())

        return tripRepository.fetchContributedTripsFromFirebase(currentUser)
            .map (::processTripIdentifiers)
    }

    suspend fun processTripIdentifiers(tripIdentifiers: List<TripIdentifier>): List<TripIdentifier> {

        return processTripIdentifiersUseCase(
            tripIdentifiers = tripIdentifiers
        )
    }
}
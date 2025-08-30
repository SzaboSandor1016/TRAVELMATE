package com.example.features.trips.domain.usecases

import com.example.core.auth.domain.FirebaseAuthenticationSource
import com.example.features.trips.domain.models.TripIdentifierTripsDomainModel
import com.example.features.trips.domain.repositories.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class GetRemoteContributedTripsUseCase(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(): Flow<List<TripIdentifierTripsDomainModel.Remote>> {

        return tripRepository.fetchContributedTripsFromFirebase()

            /*.map {
                processTripIdentifiers(
                    tripIdentifiers = it as List<TripIdentifierTripsDomainModel.Remote>,
                    currentUser = currentUser
                )
            }*/
    }

    /*suspend fun processTripIdentifiers(
        tripIdentifiers: List<TripIdentifierTripsDomainModel.Remote>,
        currentUser: String
    ): List<TripIdentifierTripsDomainModel.Remote> {

        return processTripIdentifiersUseCase(
            tripIdentifiers = tripIdentifiers,
            userUid = currentUser
        )
    }*/
}
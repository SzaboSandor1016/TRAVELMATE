package com.example.features.trips.domain.usecases

import com.example.features.trips.domain.models.TripIdentifierTripsDomainModel
import com.example.features.trips.domain.repositories.TripRepository
import kotlinx.coroutines.flow.Flow

class GetRemoteTripsUseCase(
    private val tripRepository: TripRepository,
    //private val firebaseAuthenticationSource: FirebaseAuthenticationSource,
    //private val processTripIdentifiersUseCase: ProcessTripIdentifiersUseCase
) {
    suspend operator fun invoke(): Flow<List<TripIdentifierTripsDomainModel.Remote>> {

        return tripRepository.fetchMyTripsFromFirebase()/*.map {
            processTripIdentifiers(
                it as List<TripIdentifierTripsDomainModel.Remote>,
                currentUser
            )
        }*/
    }/*

    suspend fun processTripIdentifiers(
        tripIdentifiers: List<TripIdentifierTripsDomainModel.Remote>,
        userUid: String
    ): List<TripIdentifierTripsDomainModel.Remote> {

        return processTripIdentifiersUseCase(
            tripIdentifiers = tripIdentifiers,
            userUid = userUid
        )
    }*/
}
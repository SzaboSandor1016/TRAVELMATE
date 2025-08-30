package com.example.features.trips.domain.usecases

import com.example.features.trips.domain.models.TripIdentifierTripsDomainModel
import com.example.features.trips.domain.models.TripTripsDomainModel
import com.example.features.trips.domain.repositories.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetSelectedTripDataUseCase(
    private val tripRepository: TripRepository
) {

    suspend operator fun invoke(
        tripIdentifier: TripIdentifierTripsDomainModel
    ): Flow<TripTripsDomainModel> {

        return when(tripIdentifier) {

            is TripIdentifierTripsDomainModel.Local -> flowOf(tripRepository.getCurrentLocalTripData(
                localTripUUID = tripIdentifier.uuid
            ))
            is TripIdentifierTripsDomainModel.Remote -> tripRepository.getCurrentRemoteTripData(
                remoteTripUUID = tripIdentifier.uuid
            )
            is TripIdentifierTripsDomainModel.Default -> flowOf(TripTripsDomainModel.Default)
        }
    }
}
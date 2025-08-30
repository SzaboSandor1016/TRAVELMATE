package com.example.features.trips.domain.usecases

import com.example.features.trips.domain.models.TripIdentifierTripsDomainModel
import com.example.features.trips.domain.repositories.TripRepository
import kotlinx.coroutines.flow.Flow

class GetLocalTripsUseCase(
    private val tripRepository: TripRepository
) {

    suspend operator fun invoke(): Flow<List<TripIdentifierTripsDomainModel.Local>> {

        return tripRepository.fetchAllLocalSavedTrips()
    }
}
package com.example.travel_mate.domain

import com.example.travel_mate.data.TripRepositoryImpl.TripIdentifier
import kotlinx.coroutines.flow.Flow

class GetLocalTripsUseCase(
    private val tripRepository: TripRepository
) {

    suspend operator fun invoke(): Flow<List<TripIdentifier>> {

        return tripRepository.fetchAllLocalSavedTrips()
    }
}
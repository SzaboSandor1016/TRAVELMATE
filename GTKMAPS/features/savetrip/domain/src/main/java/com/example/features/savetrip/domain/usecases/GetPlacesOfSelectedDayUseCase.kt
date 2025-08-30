package com.example.features.savetrip.domain.usecases

import com.example.features.savetrip.domain.models.PlaceSaveTripDomainModel
import com.example.features.savetrip.domain.repository.SaveTripRepository
import kotlinx.coroutines.flow.Flow

class GetPlacesOfSelectedDayUseCase(
    private val saveTripRepository: SaveTripRepository
) {

    operator fun invoke(): Flow<List<PlaceSaveTripDomainModel>> {

        return saveTripRepository.getPlacesOfDayOfTripBy()
    }
}
package com.example.features.savetrip.domain.usecases

import com.example.features.savetrip.domain.repository.SaveTripRepository
import kotlinx.coroutines.flow.Flow

class IsContainedByTripUseCase(
    private val saveTripRepository: SaveTripRepository
) {
    operator fun invoke(uuid: String): Flow<Boolean> {
        return saveTripRepository.isContainedInAssignablePlaces(
            uuid = uuid
        )
    }
}
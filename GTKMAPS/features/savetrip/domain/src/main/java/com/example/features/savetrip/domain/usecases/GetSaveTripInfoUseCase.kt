package com.example.features.savetrip.domain.usecases

import com.example.features.savetrip.domain.models.SaveTripInfoSaveTripDomainModel
import com.example.features.savetrip.domain.repository.SaveTripRepository
import kotlinx.coroutines.flow.Flow

class GetSaveTripInfoUseCase(
    private val saveTripRepository: SaveTripRepository
) {

    operator fun invoke(): Flow<SaveTripInfoSaveTripDomainModel> {

        return saveTripRepository.getSaveTripInfo()
    }
}

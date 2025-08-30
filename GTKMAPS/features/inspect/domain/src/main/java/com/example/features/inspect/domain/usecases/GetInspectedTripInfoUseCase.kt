package com.example.features.inspect.domain.usecases

import com.example.features.inspect.domain.models.info.InspectTripTripInfoInspectTripDomainModel
import com.example.features.inspect.domain.repositories.CurrentTripRepository
import kotlinx.coroutines.flow.Flow

class GetInspectedTripInfoUseCase(
    private val currentTripRepository: CurrentTripRepository
) {

    operator fun invoke(): Flow<InspectTripTripInfoInspectTripDomainModel> {

        return currentTripRepository.getCurrentTripInfo()
    }
}
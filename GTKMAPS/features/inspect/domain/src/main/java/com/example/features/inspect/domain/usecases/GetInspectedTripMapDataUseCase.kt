package com.example.features.inspect.domain.usecases

import com.example.features.inspect.domain.models.map.InspectTripMapDataInspectTripDomainModel
import com.example.features.inspect.domain.repositories.CurrentTripRepository
import kotlinx.coroutines.flow.Flow

class GetInspectedTripMapDataUseCase(
    private val currentTripRepository: CurrentTripRepository
) {
    operator fun invoke(): Flow<InspectTripMapDataInspectTripDomainModel> {

        return currentTripRepository.getCurrentTripMapData()
    }
}
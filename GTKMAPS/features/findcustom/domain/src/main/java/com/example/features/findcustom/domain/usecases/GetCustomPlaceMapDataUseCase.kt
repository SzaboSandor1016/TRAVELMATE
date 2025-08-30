package com.example.features.findcustom.domain.usecases

import com.example.features.findcustom.domain.models.CustomPlaceMapDataCustomPlaceDomainModel
import com.example.features.findcustom.domain.repositories.CustomPlaceRepository
import kotlinx.coroutines.flow.Flow

class GetCustomPlaceMapDataUseCase(
    private val customPlaceRepository: CustomPlaceRepository
) {
    operator fun invoke(): Flow<CustomPlaceMapDataCustomPlaceDomainModel> {
        return customPlaceRepository.getCustomPlaceMapData()
    }
}
package com.example.features.findcustom.domain.usecases

import com.example.features.findcustom.domain.models.CustomPlaceInfoCustomPlaceDomainModel
import com.example.features.findcustom.domain.repositories.CustomPlaceRepository
import kotlinx.coroutines.flow.Flow

class GetCustomPlaceInfoUseCase(private val customPlaceRepository: CustomPlaceRepository) {

    operator fun invoke(): Flow<CustomPlaceInfoCustomPlaceDomainModel> {
        return customPlaceRepository.getCustomPlaceInfo()
    }
}
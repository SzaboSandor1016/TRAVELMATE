package com.example.features.findcustom.domain.usecases

import com.example.features.findcustom.domain.models.PlaceCustomPlaceDomainModel
import com.example.features.findcustom.domain.repositories.CustomPlaceRepository

class GetCustomPlaceFullDataUseCase(
    private val customPlaceRepository: CustomPlaceRepository
) {

    suspend operator fun invoke(): PlaceCustomPlaceDomainModel {

        return customPlaceRepository.getCustomPlace()
    }
}
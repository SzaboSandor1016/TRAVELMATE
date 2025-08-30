package com.example.features.findcustom.domain.usecases

import com.example.features.findcustom.domain.repositories.CustomPlaceRepository

class ResetCustomPlaceUseCase(
    private val customPlaceRepository: CustomPlaceRepository
) {

    suspend operator fun invoke() {

        customPlaceRepository.resetCustomPlace()
    }
}
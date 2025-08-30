package com.example.features.savetrip.domain.usecases

import com.example.features.savetrip.domain.repository.SaveTripRepository

class SelectUnselectContributorUseCase(
    private val saveTripRepository: SaveTripRepository
) {

    suspend operator fun invoke(uid: String, select: Boolean) {
        saveTripRepository.selectUnselectContributor(
            uid= uid,
            //select = select
        )
    }
}
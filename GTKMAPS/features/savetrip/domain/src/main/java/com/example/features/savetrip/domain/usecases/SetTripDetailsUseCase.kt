package com.example.features.savetrip.domain.usecases

import com.example.features.savetrip.domain.repository.SaveTripRepository

class SetTripDetailsUseCase(
    private val saveTripRepository: SaveTripRepository
) {

    suspend operator fun invoke(
        title: String,
        startDate: String?,
        note: String?
    ) {

        saveTripRepository.setTripDetails(
            title = title,
            startDate = startDate,
            note = note
        )
    }
}
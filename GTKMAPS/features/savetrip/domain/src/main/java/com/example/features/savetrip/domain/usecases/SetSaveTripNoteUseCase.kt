package com.example.features.savetrip.domain.usecases

import com.example.features.savetrip.domain.repository.SaveTripRepository

class SetSaveTripNoteUseCase(
    private val saveTripRepository: SaveTripRepository
) {

    suspend operator fun invoke(note: String) {

        saveTripRepository.setTripNote(
            note = note
        )
    }
}
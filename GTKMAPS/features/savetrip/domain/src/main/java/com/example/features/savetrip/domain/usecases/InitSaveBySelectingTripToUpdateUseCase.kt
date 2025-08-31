package com.example.features.savetrip.domain.usecases

import android.util.Log
import com.example.features.savetrip.domain.models.SaveTripDataSaveTripDomainModel
import com.example.features.savetrip.domain.repository.SaveTripRepository

class InitSaveBySelectingTripToUpdateUseCase(
    private val saveTripRepository: SaveTripRepository
) {

    suspend operator fun invoke(tripData: SaveTripDataSaveTripDomainModel) {

        saveTripRepository.initSaveBySelectingTripToUpdate(
            tripData = tripData
        )

        Log.d("initTripUpdate", "executed")
    }
}
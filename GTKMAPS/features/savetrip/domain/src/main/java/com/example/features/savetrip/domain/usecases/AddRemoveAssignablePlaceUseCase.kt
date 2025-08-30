package com.example.features.savetrip.domain.usecases

import com.example.features.savetrip.domain.models.PlaceSaveTripDomainModel
import com.example.features.savetrip.domain.repository.SaveTripRepository

class AddRemoveAssignablePlaceUseCase(
    private val saveTripRepository: SaveTripRepository
) {

    suspend operator fun invoke(place: PlaceSaveTripDomainModel) {

        saveTripRepository.addRemoveAssignablePlace(
            place = place
        )
    }
}
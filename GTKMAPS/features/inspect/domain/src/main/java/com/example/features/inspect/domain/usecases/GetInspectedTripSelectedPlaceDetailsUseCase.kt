package com.example.features.inspect.domain.usecases

import com.example.features.inspect.domain.models.PlaceDetailsInspectTripDomainModel
import com.example.features.inspect.domain.repositories.CurrentTripRepository

class GetInspectedTripSelectedPlaceDetailsUseCase(
    private val currentTripRepository: CurrentTripRepository
) {
    operator fun invoke(placeUUID: String): PlaceDetailsInspectTripDomainModel? {

        return currentTripRepository.getInspectedTripPlaceDetails(
            placeUUID = placeUUID
        )
    }
}
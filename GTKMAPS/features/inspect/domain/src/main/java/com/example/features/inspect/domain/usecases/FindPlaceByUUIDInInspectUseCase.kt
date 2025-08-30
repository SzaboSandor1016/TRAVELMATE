package com.example.features.inspect.domain.usecases

import com.example.features.inspect.domain.models.PlaceInspectTripDomainModel
import com.example.features.inspect.domain.repositories.CurrentTripRepository

class FindPlaceByUUIDInInspectUseCase(
    private val currentTripRepository: CurrentTripRepository
) {

    operator fun invoke(placeUUID: String): PlaceInspectTripDomainModel? {

        return currentTripRepository.getPlaceByUUID(
            placeUUID = placeUUID
        )
    }
}
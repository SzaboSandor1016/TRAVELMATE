package com.example.features.inspect.domain.usecases

import com.example.features.inspect.domain.models.TripInspectTripDomainModel
import com.example.features.inspect.domain.repositories.CurrentTripRepository

class SetInspectedTripUseCase(
    private val currentTripRepository: CurrentTripRepository
) {
    operator fun invoke(inspectedTrip: TripInspectTripDomainModel.Inspected) {

        currentTripRepository.setCurrentTrip(
            currentTrip = inspectedTrip
        )
    }
}
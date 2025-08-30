package com.example.features.trips.domain.usecases

import com.example.features.trips.domain.models.TripIdentifierTripsDomainModel

class DeleteTripUseCase(
    private val deleteLocalTripUseCase: DeleteLocalTripUseCase,
    private val deleteRemoteTripUseCase: DeleteRemoteTripUseCase
){
    suspend operator fun invoke(tripIdentifier: TripIdentifierTripsDomainModel) {

        if(tripIdentifier is TripIdentifierTripsDomainModel.Local) {
            deleteLocalTripUseCase(tripUUID = tripIdentifier.uuid)
        }

        if (tripIdentifier is TripIdentifierTripsDomainModel.Remote) {
            deleteRemoteTripUseCase(
                tripUuid = tripIdentifier.uuid,
                creatorUid = tripIdentifier.creatorUID
            )
        }
    }
}
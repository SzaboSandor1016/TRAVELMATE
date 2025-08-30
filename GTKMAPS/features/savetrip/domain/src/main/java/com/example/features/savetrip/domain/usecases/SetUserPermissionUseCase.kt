package com.example.features.savetrip.domain.usecases

import com.example.features.savetrip.domain.repository.SaveTripRepository

class SetUserPermissionUseCase(
    private val saveTripRepository: SaveTripRepository
) {

    suspend operator fun invoke(uid: String, permissionToUpdate: Boolean) {

        saveTripRepository.setUserPermission(
            userUid = uid,
            permissionToUpdate = permissionToUpdate
        )
    }
}
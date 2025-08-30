package com.example.features.savetrip.domain.usecases

import com.example.features.user.domain.repositories.UserRepository
import com.example.features.savetrip.domain.models.PlaceSaveTripDomainModel
import com.example.features.savetrip.domain.repository.SaveTripRepository

class InitSaveFromSearch(
    private val saveTripRepository: SaveTripRepository,
    //private val userRepository: UserRepository,
) {

    suspend operator fun invoke(startPlace: PlaceSaveTripDomainModel) {

        //val userUID = userRepository.getCurrentUserID()

        saveTripRepository.initSaveFromSearchWithStartPlace(
            startPlace = startPlace
        )
    }
}
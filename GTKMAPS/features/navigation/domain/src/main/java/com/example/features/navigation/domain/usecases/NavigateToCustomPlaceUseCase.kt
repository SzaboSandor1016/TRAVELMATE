package com.example.features.navigation.domain.usecases

import com.example.features.navigation.domain.mappers.toCoordinatesNavigationDomainModel
import com.example.features.navigation.domain.models.CoordinatesNavigationDomainModel
import com.example.features.navigation.domain.repositories.NavigationRepository

class NavigateToCustomPlaceUseCase(
    private val navigationRepository: NavigationRepository
) {

    operator fun invoke(latitude: Double, longitude:Double,transportMode: String) {

        navigationRepository.initNavigation(
            destinationCoordinates = listOf(CoordinatesNavigationDomainModel(
                latitude = latitude,
                longitude = longitude
            )),
            navigationMode = transportMode
        )

        navigationRepository.navigateToPlaceInRoute()
    }
}
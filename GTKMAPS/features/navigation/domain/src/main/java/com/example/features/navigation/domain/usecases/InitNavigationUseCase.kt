package com.example.features.navigation.domain.usecases

import com.example.features.navigation.domain.mappers.toCoordinatesNavigationDomainModel
import com.example.features.navigation.domain.repositories.NavigationRepository
import com.example.features.route.domain.usecases.GetCurrentRouteNodesCoordinatesUseCase

class InitNavigationUseCase(
    private val navigationRepository: NavigationRepository,
    private val getCurrentRouteNodesCoordinatesUseCase: GetCurrentRouteNodesCoordinatesUseCase
) {

    operator fun invoke(transportMode: String) {

        val currentRouteNodesCoordinates = getCurrentRouteNodesCoordinatesUseCase()

        navigationRepository.initNavigation(
            destinationCoordinates = currentRouteNodesCoordinates.subList(
                fromIndex = 1, toIndex = currentRouteNodesCoordinates.size - 1
            ).map {
                it.toCoordinatesNavigationDomainModel()
            },
            navigationMode = transportMode
        )
    }
}
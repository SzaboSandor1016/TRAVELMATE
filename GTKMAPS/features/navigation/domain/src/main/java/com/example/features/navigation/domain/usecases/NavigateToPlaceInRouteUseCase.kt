package com.example.features.navigation.domain.usecases

import com.example.features.navigation.domain.mappers.toCoordinatesNavigationDomainModel
import com.example.features.navigation.domain.repositories.NavigationRepository

class NavigateToPlaceInRouteUseCase(
    private val navigationRepository: NavigationRepository
) {
    operator fun invoke() {

        navigationRepository.navigateToPlaceInRoute()
    }
}
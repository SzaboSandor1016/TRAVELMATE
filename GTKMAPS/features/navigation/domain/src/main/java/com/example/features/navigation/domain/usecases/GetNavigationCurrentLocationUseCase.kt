package com.example.features.navigation.domain.usecases

import com.example.features.navigation.domain.models.CurrentLocationNavigationDomainModel
import com.example.features.navigation.domain.repositories.NavigationRepository
import kotlinx.coroutines.flow.Flow

class GetNavigationCurrentLocationUseCase(
    private val navigationRepository: NavigationRepository
) {

    operator fun invoke(): Flow<CurrentLocationNavigationDomainModel> {
        return navigationRepository.getNavigationCurrentLocation()
    }
}
package com.example.features.navigation.domain.usecases

import com.example.features.navigation.domain.models.NavigationMapDataNavigationDomainModel
import com.example.features.navigation.domain.repositories.NavigationRepository
import kotlinx.coroutines.flow.Flow

class GetNavigationMapDataUseCase(
    private val navigationRepository: NavigationRepository
) {
    operator fun invoke(): Flow<NavigationMapDataNavigationDomainModel> {
        return navigationRepository.getNavigationMapData()
    }
}
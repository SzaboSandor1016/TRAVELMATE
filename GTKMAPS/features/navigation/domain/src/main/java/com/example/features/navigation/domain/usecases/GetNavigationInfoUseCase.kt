package com.example.features.navigation.domain.usecases

import com.example.features.navigation.domain.models.NavigationInfoNavigationDomainModel
import com.example.features.navigation.domain.repositories.NavigationRepository
import kotlinx.coroutines.flow.Flow

class GetNavigationInfoUseCase(
    private val navigationRepository: NavigationRepository
) {
    operator fun invoke(): Flow<NavigationInfoNavigationDomainModel> {
        return navigationRepository.getNavigationInfo()
    }
}
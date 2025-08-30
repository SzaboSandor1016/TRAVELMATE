package com.example.features.navigation.domain.usecases

import com.example.features.navigation.domain.repositories.NavigationRepository

class EndNavigationUseCase(
    private val navigationRepository: NavigationRepository
) {

    operator fun invoke() {

        navigationRepository.stopNavigationJobs()

        navigationRepository.resetNavigation()
    }
}
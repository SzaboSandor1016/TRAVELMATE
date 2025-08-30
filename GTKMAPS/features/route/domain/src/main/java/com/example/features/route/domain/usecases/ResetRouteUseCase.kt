package com.example.features.route.domain.usecases

import com.example.features.route.domain.repositories.RouteRepository

class ResetRouteUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(all: Boolean) {

        routeRepository.resetRoute(all = all)
    }
}
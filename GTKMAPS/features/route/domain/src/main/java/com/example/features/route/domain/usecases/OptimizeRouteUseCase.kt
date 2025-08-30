package com.example.features.route.domain.usecases

import com.example.features.route.domain.repositories.RouteRepository

class OptimizeRouteUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke() {

        routeRepository.optimizeRoute()
    }
}
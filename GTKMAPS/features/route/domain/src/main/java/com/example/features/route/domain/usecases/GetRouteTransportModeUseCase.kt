package com.example.features.route.domain.usecases

import com.example.features.route.domain.repositories.RouteRepository

class GetRouteTransportModeUseCase(
    private val routeRepository: RouteRepository
) {

    operator fun invoke(): String {

        return routeRepository.getRouteTransportMode()
    }
}
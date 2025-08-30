package com.example.features.route.domain.usecases

import com.example.features.route.domain.repositories.RouteRepository

class SetRouteTransportModeUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(transportModeIndex: Int) {

        routeRepository.setRouteTransportMode(
            index = transportModeIndex
        )
    }
}
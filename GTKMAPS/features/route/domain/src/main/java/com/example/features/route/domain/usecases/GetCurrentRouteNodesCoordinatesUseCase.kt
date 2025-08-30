package com.example.features.route.domain.usecases

import com.example.features.route.domain.models.CoordinatesRouteDomainModel
import com.example.features.route.domain.repositories.RouteRepository

class GetCurrentRouteNodesCoordinatesUseCase(
    private val routeRepository: RouteRepository
) {
    operator fun invoke(): List<CoordinatesRouteDomainModel> {

        return routeRepository.getCurrentRouteNodesCoordinates()
    }
}
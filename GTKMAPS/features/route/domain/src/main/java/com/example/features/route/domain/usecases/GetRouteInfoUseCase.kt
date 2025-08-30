package com.example.features.route.domain.usecases

import com.example.features.route.domain.models.info.RouteInfoRouteDomainModel
import com.example.features.route.domain.repositories.RouteRepository
import kotlinx.coroutines.flow.Flow

class GetRouteInfoUseCase(
    private val routeRepository: RouteRepository
) {
    operator fun invoke(): Flow<RouteInfoRouteDomainModel> {
        return routeRepository.getRouteInfo()
    }
}
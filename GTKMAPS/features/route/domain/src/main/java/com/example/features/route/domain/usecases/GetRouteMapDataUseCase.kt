package com.example.features.route.domain.usecases

import com.example.features.route.domain.models.mapdata.RouteMapDataRouteDomainModel
import com.example.features.route.domain.repositories.RouteRepository
import kotlinx.coroutines.flow.Flow

class GetRouteMapDataUseCase(
    private val routeRepository: RouteRepository
) {
    operator fun invoke(): Flow<RouteMapDataRouteDomainModel> {
        return routeRepository.getRouteMapData()
    }
}
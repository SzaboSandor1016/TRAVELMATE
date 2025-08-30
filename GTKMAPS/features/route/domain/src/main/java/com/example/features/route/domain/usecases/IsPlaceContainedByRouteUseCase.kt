package com.example.features.route.domain.usecases

import com.example.features.route.domain.repositories.RouteRepository
import kotlinx.coroutines.flow.Flow

class IsPlaceContainedByRouteUseCase(
    private val routeRepository: RouteRepository
) {

    operator fun invoke(uuid: String): Flow<Boolean> {

        return routeRepository.isPlaceContained(
            uuid = uuid
        )
    }
}
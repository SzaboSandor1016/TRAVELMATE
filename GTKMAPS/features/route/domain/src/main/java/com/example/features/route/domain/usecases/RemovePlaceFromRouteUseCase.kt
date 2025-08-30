package com.example.features.route.domain.usecases

import com.example.features.route.domain.repositories.RouteRepository

class RemovePlaceFromRouteUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(placeUUID: String) {

        routeRepository.removePlaceFromRouteByUUID(placeUUID = placeUUID)
    }
}
package com.example.features.route.domain.usecases

import com.example.features.route.domain.models.PlaceRouteDomainModel
import com.example.features.route.domain.repositories.RouteRepository

class InitRouteUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(startPlace: PlaceRouteDomainModel) {


        routeRepository.initNewRoute(
            startPlace = startPlace
        )
    }
}
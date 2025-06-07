package com.example.travel_mate.domain

import com.example.travel_mate.data.Place

class InitRouteUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(startPlace: Place) {


        routeRepository.initNewRoute(
            startPlace = startPlace
        )
    }
}
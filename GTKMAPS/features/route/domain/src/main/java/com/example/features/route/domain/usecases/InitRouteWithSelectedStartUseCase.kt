package com.example.features.route.domain.usecases

import com.example.features.route.domain.models.PlaceRouteDomainModel

class InitRouteWithSelectedStartUseCase(
    private val initRouteUseCase: InitRouteUseCase
) {

    suspend operator fun invoke(startPlace: PlaceRouteDomainModel) {

        initRouteUseCase(
            startPlace = startPlace
        )
    }
}
package com.example.travel_mate.domain

import com.example.travel_mate.data.Coordinates
import com.example.travel_mate.data.Place

class InitSearchAndRouteWithSelectedStartUseCase(
    private val initSearchUseCase: InitSearchUseCase,
    private val initRouteUseCase: InitRouteUseCase
) {

    suspend operator fun invoke(startPlace: Place, places: List<Place>) {

        initSearchUseCase(
            startPlace = startPlace,
            places = places
        )
        initRouteUseCase(
            startPlace = startPlace
        )
    }
}
package com.example.travel_mate.domain

import com.example.travel_mate.data.Place

class InitSearchUseCase(
    private val searchRepository: SearchRepository,
) {

    suspend operator fun invoke(startPlace: Place, places: List<Place>) {

        searchRepository.initNewSearch(
            startPlace = startPlace,
            places = places
        )
    }
}
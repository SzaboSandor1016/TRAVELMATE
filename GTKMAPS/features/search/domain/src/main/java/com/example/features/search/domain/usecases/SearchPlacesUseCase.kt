package com.example.features.search.domain.usecases

import com.example.features.search.domain.repositories.SearchOptionsRepository
import com.example.features.search.domain.repositories.SearchRepository

class SearchPlacesUseCase(
    private val searchRepository: SearchRepository,
    private val searchOptionsRepository: SearchOptionsRepository
) {
    suspend operator fun invoke(
        content: String,
        category: String
    ){

        val startPlace = searchRepository.getStartPlace()?: return

        val distance = searchOptionsRepository.getDistance();

        //TODO NOTE this return thing below is feels wrong maybe should be refactored
        when (distance) {

            0.0 -> {
                searchRepository.fetchPlacesByCity(
                    content = content,
                    city = startPlace.getAddress().city?: return,
                    category = category
                )

            }

            else -> {
                searchRepository.fetchPlacesByDistance(
                    distance = distance,
                    content = content,
                    centerLat = startPlace.getCoordinates().latitude,
                    centerLon = startPlace.getCoordinates().longitude,
                    category = category
                )
            }
        }
    }

}
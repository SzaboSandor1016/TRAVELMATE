package com.example.travel_mate.domain

import com.example.travel_mate.data.Coordinates
import com.example.travel_mate.data.SearchRepositoryImpl

class SearchPlacesUseCase(
    private val searchRepository: SearchRepository,
    private val searchOptionsRepository: SearchOptionsRepository
) {
    suspend operator fun invoke(
        content: String,
        coordinates: Coordinates,
        city: String,
        category: String
    ){

        val distance = searchOptionsRepository.getDistance();

        when (distance) {

            0.0 -> {
                searchRepository.fetchPlacesByCity(
                    content = content,
                    city = city,
                    category = category
                )

            }

            else -> {
                searchRepository.fetchPlacesByDistance(
                    distance = distance,
                    content = content,
                    coordinates = coordinates,
                    category = category
                )
            }
        }
    }

}
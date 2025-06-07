package com.example.travel_mate.domain

import com.example.travel_mate.data.Coordinates
import org.osmdroid.util.GeoPoint

class SetCustomPlaceUseCase(
    private val searchReverseGeoCodeUseCase: SearchReverseGeoCodeUseCase,
    private val customPlaceRepository: CustomPlaceRepository
) {

    suspend operator fun invoke(clickedPoint: GeoPoint) {

        val positionInCoordinates = Coordinates(
            latitude = clickedPoint.latitude,
            longitude = clickedPoint.longitude
        )

        searchReverseGeoCodeUseCase.invoke(positionInCoordinates).collect {

            if (it.isNotEmpty()) {
                customPlaceRepository.setCustomPlace(it[0])
            }
        }
    }
}
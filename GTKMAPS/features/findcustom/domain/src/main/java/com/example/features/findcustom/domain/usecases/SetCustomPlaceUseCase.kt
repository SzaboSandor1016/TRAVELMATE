package com.example.features.findcustom.domain.usecases

import com.example.features.findcustom.domain.mappers.mapToCustomPlace
import com.example.features.findcustom.domain.repositories.CustomPlaceRepository
import com.example.core.remotedatasources.reversegeocode.domain.repository.ReverseGeoCodeRepository
import org.osmdroid.util.GeoPoint

class SetCustomPlaceUseCase(
    private val reverseGeoCodeRepository: ReverseGeoCodeRepository,
    private val customPlaceRepository: CustomPlaceRepository
) {

    suspend operator fun invoke(clickedPoint: GeoPoint) {

        reverseGeoCodeRepository.getReverseGeoCode(
            latitude = clickedPoint.latitude,
            longitude = clickedPoint.longitude
        ).collect {

            val places = it.mapToCustomPlace()

            if (places.isNotEmpty()) {
                customPlaceRepository.setCustomPlace(places[0])
            }
        }
    }
}
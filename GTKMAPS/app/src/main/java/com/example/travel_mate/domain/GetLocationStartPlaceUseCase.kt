package com.example.travel_mate.domain

import android.util.Log
import com.example.travel_mate.data.Coordinates
import com.example.travel_mate.data.Place

class GetLocationStartPlaceUseCase(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val searchReverseGeoCodeUseCase: SearchReverseGeoCodeUseCase,
    private val initSearchUseCase: InitSearchUseCase
) {

    suspend operator fun invoke() {

        getCurrentLocationUseCase().collect{ location ->

            val coordinates = when(location) {

                null -> Coordinates()

                else -> Coordinates(latitude = location.latitude, longitude = location.longitude)
            }

            searchReverseGeoCodeUseCase.invoke(coordinates).collect{ reverseGeoCode ->

                if (reverseGeoCode.isNotEmpty()) {

                    val places = emptyList<Place>()

                    initSearchUseCase(
                        startPlace = reverseGeoCode[0],
                        places = places
                    )
                }
            }
        }
    }
}
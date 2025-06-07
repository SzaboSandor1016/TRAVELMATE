package com.example.travel_mate.domain

import android.util.Log
import com.example.travel_mate.data.Address
import com.example.travel_mate.data.Coordinates
import com.example.travel_mate.data.PhotonResponse
import com.example.travel_mate.data.Place
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchReverseGeoCodeUseCase(
    private val searchRepository: SearchRepository
) {

    suspend operator fun invoke(coordinates: Coordinates): Flow<List<Place>> =

        searchRepository.getReverseGeoCode(
            coordinates = coordinates
        ).map(::processPhotonResponse)

    /** [processPhotonResponse]
     * process the data returned by the network request to Photon
     * accepts the feature list of [PhotonResponse]
     * Returns a list of [Place]s
     */
    private fun processPhotonResponse(response: PhotonResponse): List<Place> {

        val places: ArrayList<Place> = ArrayList()

        for (feature in response.features) {

            val startPlace = Place()
            val placeCoordinates = Coordinates()
            val address = Address()

            if (feature.geometry != null) {

                val geometry = feature.geometry

                if (geometry != null) {

                    val coordinates = geometry.coordinates

                    if (coordinates != null) {
                        placeCoordinates.setLatitude(coordinates[1].toDouble())
                        placeCoordinates.setLongitude(coordinates[0].toDouble())
                    }
                }

            }
            if (feature.properties != null) {

                val properties = feature.properties

                if (properties.name != null) {
                    startPlace.setName(properties.name)
                    Log.d("Address", properties.name.toString())
                }
                if (properties.city != null) {
                    address.setCity(properties.city)
                    Log.d("Address_city", properties.city.toString())
                }
                if (properties.street != null) {
                    address.setStreet(properties.street)
                    Log.d("Address_street", properties.street.toString())
                }
                if (properties.houseNumber != null) {
                    address.setHouseNumber(properties.houseNumber)
                    Log.d("Address_hn", properties.houseNumber.toString())
                }
                if (properties.country != null) {
                    address.setCountry(properties.country)
                }

            }

            startPlace.setUUId()
            startPlace.setAddress(address)
            startPlace.setCoordinates(placeCoordinates)
            places.add(startPlace)


        }
        return places
    }
}

package com.example.core.remotedatasources.reversegeocode.data.repository

import com.example.core.remotedatasources.reversegeocode.domain.datasource.ReverseGeoCodeDataSource
import com.example.core.remotedatasources.reversegeocode.domain.repository.ReverseGeoCodeRepository
import com.example.remotedatasources.responses.ReverseGeoCodeResponse
import kotlinx.coroutines.flow.Flow

class ReverseGeoCodeRepositoryImpl(private val reverseGeoCodeDataSource: ReverseGeoCodeDataSource): ReverseGeoCodeRepository {

    override suspend fun getReverseGeoCode(latitude: Double, longitude: Double): Flow<ReverseGeoCodeResponse> {

        return reverseGeoCodeDataSource.getReverseGeoCode(
            latitude = latitude,
            longitude = longitude
        )
    }

    /** [processReverseGeoCode]
     * process the data returned by the network request to Photon
     * accepts the feature list of [com.example.domain.models.PhotonResponse]
     * Returns a list of [com.example.model.Place]s
     */
    /*private fun processReverseGeoCode(response: ReverseGeoCodeResponse): List<Place> {

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
                if (properties.locality != null) {
                    address.setCity(properties.locality)
                    Log.d("Address_city", properties.locality.toString())
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
    }*/
}
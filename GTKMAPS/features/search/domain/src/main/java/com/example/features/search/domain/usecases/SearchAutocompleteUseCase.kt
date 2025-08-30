package com.example.features.search.domain.usecases

import com.example.features.search.domain.models.searchmodels.AddressSearchDomainModel
import com.example.features.search.domain.models.searchmodels.CoordinatesSearchDomainModel
import com.example.features.search.domain.models.searchmodels.PlaceSearchDomainModel
import com.example.features.search.domain.repositories.SearchRepository
import com.example.remotedatasources.responses.PhotonResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class SearchAutocompleteUseCase(
    private val searchRepository: SearchRepository
) {

    suspend operator fun invoke(query: String): Flow<List<PlaceSearchDomainModel>> {

        return searchRepository.searchAutocomplete(query).map(::processPhotonResponse)
    }
    /** [processPhotonResponse]
     * process the data returned by the network request to Photon
     * accepts the feature list of [com.example.remotedatasources.responses.PhotonResponse]
     * Returns a list of [com.example.model.Place]s
     */
    private fun processPhotonResponse(response: PhotonResponse): ArrayList<PlaceSearchDomainModel> {

        val places: ArrayList<PlaceSearchDomainModel> = ArrayList()

        for (feature in response.features) {

            val placeCoordinates = feature.geometry?.coordinates?.let {
                CoordinatesSearchDomainModel(
                    it[1].toDouble(),
                    it[0].toDouble()
                )
            }?: CoordinatesSearchDomainModel()

            val address = feature.properties?.let {
                AddressSearchDomainModel(
                    city = it.city,
                    street = it.street,
                    houseNumber = it.houseNumber,
                    country = it.country
                )
            }?: AddressSearchDomainModel()

            /*if (feature.geometry != null) {

                val geometry = feature.geometry

                if (geometry != null) {

                    val coordinates = geometry.coordinates

                    if (coordinates != null) {
                        placeCoordinates.latitude = (coordinates[1].toDouble())
                        placeCoordinates.setLongitude(coordinates[0].toDouble())
                    }
                }

            }*/
            /*if (feature.properties != null) {

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

            }*/

            val startPlace = PlaceSearchDomainModel(
                uUID = UUID.randomUUID().toString(),
                name = feature.properties?.name?: "",
                address = address,
                coordinates = placeCoordinates,
                cuisine = null,
                openingHours = null,
                charge = null,
                category = "start"
            )

            /*startPlace.setUUId()
            startPlace.setAddress(address)
            startPlace.setCoordinates(placeCoordinates)*/
            places.add(startPlace)


        }
        return places
    }
}

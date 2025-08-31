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

            places.add(startPlace)
        }

        return places
    }
}

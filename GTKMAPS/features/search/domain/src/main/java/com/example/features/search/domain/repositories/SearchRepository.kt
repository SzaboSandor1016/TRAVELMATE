package com.example.features.search.domain.repositories

import com.example.features.search.domain.models.searchmodels.CoordinatesSearchDomainModel
import com.example.features.search.domain.models.searchmodels.PlaceSearchDomainModel
import com.example.features.search.domain.models.searchmodels.SearchDataSearchDomainModel
import com.example.features.search.domain.models.searchmodels.PlaceDataSearchDomainModel
import com.example.features.search.domain.models.searchmodels.SearchStartInfoSearchDomainModel
import com.example.features.search.domain.models.searchmodels.SearchSearchDomainModel
import com.example.features.search.domain.models.searchmodels.SearchStateSearchDomainModel
import com.example.remotedatasources.responses.PhotonResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SearchRepository {


    fun getSearchStartInfo(): Flow<SearchStartInfoSearchDomainModel>

    fun getSearchStartData(): Flow<PlaceDataSearchDomainModel?>

    fun getSearchPlacesData(): Flow<SearchDataSearchDomainModel>

    suspend fun searchAutocomplete(query: String): Flow<PhotonResponse>

    suspend fun  resetSearchDetails(all: Boolean)

    suspend fun initNewSearch(
        startPlace: PlaceSearchDomainModel
    )

    fun getPlaceByUUID(placeUUID: String): PlaceSearchDomainModel?

    suspend fun getCurrentPlaceByUUID(uuid: String): PlaceSearchDomainModel?

    fun getStartPlace(): PlaceSearchDomainModel?

    suspend fun removePlacesByCategory(category: String)

    suspend fun fetchPlacesByCity(
        content: String,
        city: String,
        category: String
    )

    suspend fun fetchPlacesByDistance(
        distance: Double,
        content: String,
        centerLat: Double,
        centerLon: Double,
        category: String
    )
}
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

    //val searchState: StateFlow<SearchStateSearchDomainModel>

    fun getSearchStartInfo(): Flow<SearchStartInfoSearchDomainModel>

    fun getSearchStartData(): Flow<PlaceDataSearchDomainModel?>

    fun getSearchPlacesData(): Flow<SearchDataSearchDomainModel>

    /*suspend fun testInitNewSearch(
        startPlace: PlaceSearchDomainModel,
        places: List<PlaceSearchDomainModel>
    ): SearchSearchDomainModel*/

    /*suspend fun testRemovePlacesByCategory(category: String): SearchSearchDomainModel

    suspend fun testResetSearchDetails(all: Boolean): SearchSearchDomainModel

    suspend fun testFetchPlacesByCity(
        content: String,
        city: String,
        category: String
    ): SearchSearchDomainModel*/

    /*suspend fun testFetchPlacesByDistance(
        distance: Double,
        content: String,
        coordinates: CoordinatesSearchDomainModel,
        category: String
    ): SearchSearchDomainModel*/

    //suspend fun getSearchStartPlace(): PlaceSearchDomainModel?

    //suspend fun getSearchPlaces(): List<PlaceSearchDomainModel>

    suspend fun searchAutocomplete(query: String): Flow<PhotonResponse>

    //suspend fun setSearchStartPlace(startPlace: PlaceSearchDomainModel)

    suspend fun  resetSearchDetails(all: Boolean)

    //suspend fun  resetRouteDetails()

    suspend fun initNewSearch(
        startPlace: PlaceSearchDomainModel,/*
        places: List<PlaceSearchDomainModel>*/
    )

    //suspend fun resetCurrentPlace()

    fun getPlaceByUUID(placeUUID: String): PlaceSearchDomainModel?

    suspend fun getCurrentPlaceByUUID(uuid: String): PlaceSearchDomainModel?

    fun getStartPlace(): PlaceSearchDomainModel?

    //fun getPlacesContainedByTrip(): List<PlaceSearchDomainModel>

    /*suspend fun addRemovePlaceToTrip(uuid: String)

    suspend fun addRemovePlaceToRoute(uuid: String): PlaceSearchDomainModel

    suspend fun clearPlacesAddedToTrip()

    suspend fun clearPlacesAddedToRoute()*/

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
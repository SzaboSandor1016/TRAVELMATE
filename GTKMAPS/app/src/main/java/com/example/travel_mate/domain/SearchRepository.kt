package com.example.travel_mate.domain

import com.example.travel_mate.data.Coordinates
import com.example.travel_mate.data.PhotonResponse
import com.example.travel_mate.data.Place
import com.example.travel_mate.data.ReverseGeoCodeResponse
import com.example.travel_mate.data.Search
import com.example.travel_mate.data.SearchRepositoryImpl.SearchState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SearchRepository {

    val searchState: StateFlow<SearchState>

    suspend fun testInitNewSearch(startPlace: Place, places: List<Place>): Search

    suspend fun testRemovePlacesByCategory(category: String): Search

    suspend fun testResetSearchDetails(all: Boolean): Search

    suspend fun testFetchPlacesByCity(
        content: String,
        city: String,
        category: String
    ): Search

    suspend fun testFetchPlacesByDistance(
        distance: Double,
        content: String,
        coordinates: Coordinates,
        category: String
    ): Search

    suspend fun getSearchStartPlace(): Place?

    suspend fun getSearchPlaces(): List<Place>

    suspend fun searchAutocomplete(query: String): Flow<PhotonResponse>

    suspend fun setSearchStartPlace(startPlace: Place)

    suspend fun  resetSearchDetails(all: Boolean)

    suspend fun  resetRouteDetails()

    suspend fun initNewSearch(startPlace: Place, places: List<Place>)

    suspend fun resetCurrentPlace()

    suspend fun getCurrentPlaceByUUID(uuid: String)

    fun getStartPlace(): Place?

    fun getPlacesContainedByTrip(): List<Place>

    suspend fun addRemovePlaceToTrip(uuid: String)

    suspend fun addRemovePlaceToRoute(uuid: String): Place

    suspend fun clearPlacesAddedToTrip()

    suspend fun clearPlacesAddedToRoute()

    suspend fun removePlacesByCategory(category: String)

    suspend fun fetchPlacesByCity(
        content: String,
        city: String,
        category: String
    )

    suspend fun fetchPlacesByDistance(
        distance: Double,
        content: String,
        coordinates: Coordinates,
        category: String
    )


    suspend fun getReverseGeoCode(coordinates: Coordinates): Flow<ReverseGeoCodeResponse>

}
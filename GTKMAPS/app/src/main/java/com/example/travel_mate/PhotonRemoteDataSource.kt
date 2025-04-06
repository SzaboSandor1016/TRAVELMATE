package com.example.travel_mate

interface PhotonRemoteDataSource {

    suspend fun getStartPlaces(query: String): List<Place>

    suspend fun getReverseGeoCode(coordinates: Coordinates): List<Place>
}
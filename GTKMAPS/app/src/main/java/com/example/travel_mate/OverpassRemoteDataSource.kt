package com.example.travel_mate

interface OverpassRemoteDataSource {

    suspend fun fetchPlacesByCity(content: String, city: String, category: String): List<Place>

    suspend fun fetchPlacesByCoordinates(content: String, lat: String, lon: String, dist: Double,category: String): List<Place>
}
package com.example.travel_mate.data

import com.example.travel_mate.data.Place

interface OverpassRemoteDataSource {

    suspend fun fetchPlacesByCity(content: String, city: String, category: String): List<Place>

    suspend fun fetchPlacesByCoordinates(content: String, lat: String, lon: String, dist: Double,category: String): List<Place>
}
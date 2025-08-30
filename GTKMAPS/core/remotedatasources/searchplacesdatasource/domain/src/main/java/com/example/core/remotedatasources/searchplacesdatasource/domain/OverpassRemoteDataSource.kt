package com.example.core.remotedatasources.searchplacesdatasource.domain

import com.example.remotedatasources.responses.OverpassResponse
import kotlinx.coroutines.flow.Flow

interface OverpassRemoteDataSource {

    suspend fun fetchPlacesByCity(content: String, city: String, category: String): Flow<OverpassResponse>

    suspend fun fetchPlacesByCoordinates(content: String, lat: String, lon: String, dist: Double,category: String): Flow<OverpassResponse>
}
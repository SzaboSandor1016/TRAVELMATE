package com.example.core.remotedatasources.searchstartdatasource.domain

import com.example.remotedatasources.responses.PhotonResponse
import kotlinx.coroutines.flow.Flow

interface PhotonRemoteDataSource {

    suspend fun getStartPlaces(query: String): Flow<PhotonResponse>

    suspend fun getReverseGeoCode(latitude: Double, longitude: Double): Flow<PhotonResponse>
}
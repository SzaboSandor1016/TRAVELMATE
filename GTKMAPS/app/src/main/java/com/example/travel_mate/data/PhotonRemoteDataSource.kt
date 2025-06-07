package com.example.travel_mate.data

import kotlinx.coroutines.flow.Flow

interface PhotonRemoteDataSource {

    suspend fun getStartPlaces(query: String): Flow<PhotonResponse>

    suspend fun getReverseGeoCode(coordinates: Coordinates): Flow<PhotonResponse>
}
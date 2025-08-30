package com.example.core.remotedatasources.reversegeocode.domain.repository

import com.example.remotedatasources.responses.ReverseGeoCodeResponse
import kotlinx.coroutines.flow.Flow

interface ReverseGeoCodeRepository {

    suspend fun getReverseGeoCode(latitude: Double, longitude: Double): Flow<ReverseGeoCodeResponse>
}
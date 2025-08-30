package com.example.core.remotedatasources.reversegeocode.domain.datasource

import com.example.remotedatasources.responses.ReverseGeoCodeResponse
import kotlinx.coroutines.flow.Flow

interface ReverseGeoCodeDataSource {

    suspend fun getReverseGeoCode(
        latitude: Double,
        longitude: Double
    ): Flow<ReverseGeoCodeResponse>
}
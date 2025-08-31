package com.example.core.remotedatasources.reversegeocode.data.repository

import com.example.core.remotedatasources.reversegeocode.domain.datasource.ReverseGeoCodeDataSource
import com.example.core.remotedatasources.reversegeocode.domain.repository.ReverseGeoCodeRepository
import com.example.remotedatasources.responses.ReverseGeoCodeResponse
import kotlinx.coroutines.flow.Flow

class ReverseGeoCodeRepositoryImpl(private val reverseGeoCodeDataSource: ReverseGeoCodeDataSource): ReverseGeoCodeRepository {

    override suspend fun getReverseGeoCode(latitude: Double, longitude: Double): Flow<ReverseGeoCodeResponse> {

        return reverseGeoCodeDataSource.getReverseGeoCode(
            latitude = latitude,
            longitude = longitude
        )
    }
}
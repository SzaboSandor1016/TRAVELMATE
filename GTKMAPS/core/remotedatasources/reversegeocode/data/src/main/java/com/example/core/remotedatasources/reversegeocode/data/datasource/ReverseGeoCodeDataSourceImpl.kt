package com.example.core.remotedatasources.reversegeocode.data.datasource

import com.example.core.remotedatasources.reversegeocode.domain.datasource.ReverseGeoCodeDataSource
import com.example.remotedatasources.ORSConstants.Companion.API_KEY
import com.example.remotedatasources.ORSConstants.Companion.API_URL
import com.example.remotedatasources.Requests
import com.example.remotedatasources.responses.ReverseGeoCodeResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ReverseGeoCodeDataSourceImpl: ReverseGeoCodeDataSource {

    private val routeServiceRetrofit = Retrofit.Builder()
        .baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Requests.RouteServiceApi::class.java)

    override suspend fun getReverseGeoCode(latitude: Double, longitude: Double): Flow<ReverseGeoCodeResponse> {


        return flowOf(
            routeServiceRetrofit.getReverseGeoCode(
                apiKey = API_KEY,
                longitude = longitude,
                latitude = latitude,
                size = 1
            )
        )
    }
}
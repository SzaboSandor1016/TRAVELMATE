package com.example.core.remotedatasources.routedatasource.domain

import com.example.remotedatasources.responses.RouteResponse
import java.util.concurrent.Flow

interface RouteRemoteDataSource {

    suspend fun getRouteNode(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double
    ): Pair<RouteResponse, RouteResponse>
}
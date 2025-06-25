package com.example.travel_mate.data

import kotlinx.coroutines.flow.Flow

interface RouteRemoteDataSource {

    suspend fun getRouteNode(pointStart: Coordinates, pointEnd: Coordinates): RouteNode

    suspend fun getReverseGeoCode(coordinates: Coordinates): Flow<ReverseGeoCodeResponse>
}
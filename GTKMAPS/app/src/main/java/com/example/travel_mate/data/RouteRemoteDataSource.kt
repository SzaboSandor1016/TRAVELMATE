package com.example.travel_mate.data

import com.example.travel_mate.data.Coordinates

interface RouteRemoteDataSource {

    suspend fun getRouteNode(pointStart: Coordinates, pointEnd: Coordinates): RouteNode
}
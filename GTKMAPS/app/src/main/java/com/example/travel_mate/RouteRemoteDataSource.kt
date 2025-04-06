package com.example.travel_mate

import org.osmdroid.views.overlay.Polyline

interface RouteRemoteDataSource {

    suspend fun getRouteNode(pointStart: Coordinates, pointEnd: Coordinates): RouteNode
}
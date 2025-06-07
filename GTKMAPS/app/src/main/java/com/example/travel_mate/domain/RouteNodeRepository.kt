package com.example.travel_mate.domain

import com.example.travel_mate.data.Coordinates
import com.example.travel_mate.data.RouteNode

interface RouteNodeRepository {

    suspend fun getRouteNode(stop1: Coordinates, stop2: Coordinates): RouteNode
}
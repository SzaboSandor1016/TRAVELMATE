package com.example.features.route.domain.repositories

import com.example.features.route.domain.models.CoordinatesRouteDomainModel
import com.example.features.route.domain.models.RouteNodeRouteDomainModel

interface RouteNodeRepository {

    suspend fun getRouteNode(
        placeUUID: String,
        stop1: CoordinatesRouteDomainModel,
        stop2: CoordinatesRouteDomainModel
    ): RouteNodeRouteDomainModel?
}
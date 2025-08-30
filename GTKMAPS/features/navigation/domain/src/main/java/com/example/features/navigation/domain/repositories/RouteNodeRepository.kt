package com.example.features.navigation.domain.repositories

import com.example.features.navigation.domain.models.CoordinatesNavigationDomainModel
import com.example.features.navigation.domain.models.RouteNodeNavigationDomainModel

interface RouteNodeRepository {

    suspend fun getRouteNode(stop1: CoordinatesNavigationDomainModel, stop2: CoordinatesNavigationDomainModel): RouteNodeNavigationDomainModel?
}
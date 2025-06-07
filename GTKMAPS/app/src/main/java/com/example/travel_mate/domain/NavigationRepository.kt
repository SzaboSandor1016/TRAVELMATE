package com.example.travel_mate.domain

import com.example.travel_mate.data.Coordinates
import com.example.travel_mate.data.NavigationRepositoryImpl.NavigationInfoState
import com.example.travel_mate.data.NavigationRepositoryImpl.NavigationState
import com.example.travel_mate.data.RouteNode
import kotlinx.coroutines.flow.StateFlow

interface NavigationRepository {

    val navigationState: StateFlow<NavigationState>

    val navigationInfoState: StateFlow<NavigationInfoState>

    fun getCurrentNodeIndex(): Int

    fun setEndOfNavigation(isEnd: Boolean)

    fun navigateToPlaceInRoute(destination: RouteNode, transportMode: String)

    fun navigateToNextPlaceInRoute(nextRouteNode: RouteNode)

    fun navigateToCustomPlace(coordinates: Coordinates, transportMode: String)

    fun restartNavigation(goalLocationNode: RouteNode, removeData: Boolean)

    fun stopNavigationJobs(removeData: Boolean)

    fun stopNavigationJob(removeData: Boolean)

    fun stopExtrapolationLoop()

    suspend fun updateCurrentLocation(): Coordinates?
}
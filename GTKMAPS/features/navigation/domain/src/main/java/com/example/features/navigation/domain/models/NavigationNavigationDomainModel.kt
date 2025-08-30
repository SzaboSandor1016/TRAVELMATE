package com.example.features.navigation.domain.models

import org.osmdroid.views.overlay.Polyline

/*
data class NavigationDataStateModel(
    val goal: CoordinatesNavigationDomainModel? = null,
    val currentNavigationRouteNodeIndex: Int = 0,
    val navigationMode: String? = null,
    //val navigationGoal: RouteNodeNavigationDomainModel? = null,
    val routeSteps: List<RouteStepNavigationDomainModel> = emptyList(),
    val routePolyLines: Polyline? = null
)*/

sealed interface NavigationNavigationDomainModel{

    data object Default: NavigationNavigationDomainModel

    data class Arrived(
        val hasNextDestination: Boolean,
        val finalRouteStep: RouteStepNavigationDomainModel
    ): NavigationNavigationDomainModel

    data class Navigation(
        val goal: CoordinatesNavigationDomainModel,
        //val navigationGoal: RouteNodeNavigationDomainModel? = null,
        val routeSteps: List<RouteStepNavigationDomainModel> = emptyList(),
        val routePolyLines: Polyline,
        //val isStarted: Boolean = false,
        //val startedFrom: Int = 0, // 0 -> Route, 1 -> CustomPlace
        //val endOfRoute: Boolean = false,
        //val endOfNavigation: Boolean = true,
        val prevRouteStep: RouteStepNavigationDomainModel?,
        val currentRouteStep: RouteStepNavigationDomainModel
    ): NavigationNavigationDomainModel
}

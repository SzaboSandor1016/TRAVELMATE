package com.example.features.navigation.domain.models

import org.osmdroid.views.overlay.Polyline

sealed interface NavigationMapDataNavigationDomainModel {

    data object Default: NavigationMapDataNavigationDomainModel

    data object Arrived: NavigationMapDataNavigationDomainModel

    data class NavigationMapData(
        val goal: CoordinatesNavigationDomainModel,
        val route: Polyline
    ): NavigationMapDataNavigationDomainModel
}
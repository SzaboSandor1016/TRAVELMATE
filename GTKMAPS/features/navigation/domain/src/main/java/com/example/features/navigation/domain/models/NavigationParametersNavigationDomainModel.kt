package com.example.features.navigation.domain.models

data class NavigationParametersNavigationDomainModel(
    val destinationCoordinates: List<CoordinatesNavigationDomainModel> = emptyList(),
    val currentNavigationRouteNodeIndex: Int = 0,
    val navigationMode: String? = null,
) {
}
package com.example.features.navigation.domain.models

data class NavigationStateNavigationDomainModel(
    val parameters: NavigationParametersNavigationDomainModel = NavigationParametersNavigationDomainModel(),
    val navigation: NavigationNavigationDomainModel = NavigationNavigationDomainModel.Default
) {
}
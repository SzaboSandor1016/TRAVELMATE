package com.example.features.navigation.domain.models

data class RouteStepNavigationDomainModel (
    var instruction: String? = null,
    var name: String? = null,
    var distance: Int? = null,
    var duration: Int? = null,
    var type: Int? = null,
    var coordinates: CoordinatesNavigationDomainModel = CoordinatesNavigationDomainModel()
) {
}
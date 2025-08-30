package com.example.features.route.domain.models

data class RouteStepRouteDomainModel (
    var instruction: String? = null,
    var name: String? = null,
    var distance: Int? = null,
    var duration: Int? = null,
    var type: Int? = null,
    var coordinates: CoordinatesRouteDomainModel = CoordinatesRouteDomainModel()
) {
}
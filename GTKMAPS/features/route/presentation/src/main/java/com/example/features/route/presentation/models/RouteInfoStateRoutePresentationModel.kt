package com.example.features.route.presentation.models

sealed interface RouteInfoStateRoutePresentationModel {

    data object Empty: RouteInfoStateRoutePresentationModel
    data class Route(
        val routeInfo: RouteInfoRoutePresentationModel
    ): RouteInfoStateRoutePresentationModel
}
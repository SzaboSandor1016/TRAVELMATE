package com.example.travel_mate.domain

import com.example.travel_mate.data.RouteNode

class NavigateToNextPlaceUseCase(
    private val routeRepository: RouteRepository,
    private val navigationRepository: NavigationRepository
) {
    operator fun invoke() {

        val currentNodeIndex = navigationRepository.getCurrentNodeIndex()

        val currentRouteNodes = routeRepository.getCurrentRouteNodes()

        if (currentNodeIndex < currentRouteNodes.size - 1) {

            navigationRepository.setEndOfNavigation(
                isEnd = false
            )

            val nextRoute = currentRouteNodes[currentNodeIndex + 1]

            val goalLocationNode = RouteNode(
                coordinate = nextRoute.coordinate
            )

            navigationRepository.navigateToNextPlaceInRoute(
                nextRouteNode = goalLocationNode
            )
        } else {
            navigationRepository.setEndOfNavigation(
                isEnd = true
            )
        }
    }
}
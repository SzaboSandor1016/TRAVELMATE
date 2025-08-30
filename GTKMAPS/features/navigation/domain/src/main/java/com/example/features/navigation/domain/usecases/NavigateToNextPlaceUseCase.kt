package com.example.features.navigation.domain.usecases
/*
import com.example.domain.mappers.toCoordinatesNavigationDomainModel
import com.example.domain.repositories.NavigationRepository

class NavigateToNextPlaceUseCase(
    private val getCurrentRouteNodesCoordinatesUseCase: GetCurrentRouteNodesCoordinatesUseCase,
    private val navigationRepository: NavigationRepository
) {
    */
/*operator fun invoke() {

        val currentNodeIndex = navigationRepository.getCurrentNodeIndex()

        val currentRouteNodesCoordinates = getCurrentRouteNodesCoordinatesUseCase()

        if (currentNodeIndex < currentRouteNodesCoordinates.size - 1) {

            *//*
*/
/*if (currentNodeIndex == currentRouteNodesCoordinates.size - 2) {
                navigationRepository.setEndOfNavigation(
                    isEnd = true
                )
            } else {
                navigationRepository.setEndOfNavigation(
                    isEnd = false
                )
            }*//*
*/
/*

            val nextRouteCoordinates = currentRouteNodesCoordinates[currentNodeIndex + 1]


            navigationRepository.navigateToNextPlaceInRoute(
                nextRouteNodeCoordinates = nextRouteCoordinates.toCoordinatesNavigationDomainModel()
            )
        }*//*
*/
/* else {
            navigationRepository.setEndOfNavigation(
                isEnd = true
            )
        }*//*
*/
/*
    }*//*

}*/

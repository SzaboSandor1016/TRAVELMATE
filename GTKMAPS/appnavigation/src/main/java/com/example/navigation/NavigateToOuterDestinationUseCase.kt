package com.example.navigation

class NavigateToOuterDestinationUseCase(
    private val outerNavigator: OuterNavigator
) {

    operator fun invoke(outerDestination: OuterDestination) {

        outerNavigator.navigateTo(outerDestination)
    }
}
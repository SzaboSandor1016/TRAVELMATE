package com.example.features.navigation.domain.models

sealed interface NavigationInfoNavigationDomainModel {

    data object Default: NavigationInfoNavigationDomainModel

    data class Arrived(
        val hasNextDestination: Boolean,
        val finalRouteStepName: String?,
        val finalRouteStepInstruction: String?,
        val finalRouteStepInstructionType: Int
    ): NavigationInfoNavigationDomainModel

    data class NavigationInfo(
       /* val startedFrom: Int,
        val endOfRoute: Boolean,
        val endOfNavigation: Boolean,*/
        val currentStepName: String?,
        val currentStepInstruction: String?,
        val currentStepInstructionType: Int,
        val prevStepName: String?,
        val prevStepInstruction: String?,
        val prevStepInstructionType: Int?,
    ): NavigationInfoNavigationDomainModel
}
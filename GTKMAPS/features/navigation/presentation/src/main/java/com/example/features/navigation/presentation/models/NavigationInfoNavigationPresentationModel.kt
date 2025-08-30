package com.example.features.navigation.presentation.models

sealed interface NavigationInfoNavigationPresentationModel {

    data object Default: NavigationInfoNavigationPresentationModel

    data class Navigation(
        /*val startedFrom: Int,
        val endOfRoute: Boolean,
        val endOfNavigation: Boolean,*/
        val currentStepName: String?,
        val currentStepInstruction: String?,
        val currentStepInstructionType: Int,
        val prevStepName: String?,
        val prevStepInstruction: String?,
        val prevStepInstructionType: Int?,
    ): NavigationInfoNavigationPresentationModel

    data class Arrived(
        val hasNextDestination: Boolean,
        val finalStepName: String?,
        val finalStepInstruction: String?,
        val finalStepInstructionType: Int,
    ): NavigationInfoNavigationPresentationModel
}
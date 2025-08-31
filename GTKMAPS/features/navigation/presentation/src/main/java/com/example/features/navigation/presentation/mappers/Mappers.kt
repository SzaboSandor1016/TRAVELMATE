package com.example.features.navigation.presentation.mappers

import com.example.features.navigation.domain.models.NavigationInfoNavigationDomainModel
import com.example.features.navigation.presentation.models.NavigationInfoNavigationPresentationModel


fun NavigationInfoNavigationDomainModel.NavigationInfo.toNavigationInfoPresentationModel(): NavigationInfoNavigationPresentationModel.Navigation {

    return NavigationInfoNavigationPresentationModel.Navigation(
        currentStepName = this.currentStepName,
        currentStepInstruction = this.currentStepInstruction,
        currentStepInstructionType = this.currentStepInstructionType,
        prevStepName = this.prevStepName,
        prevStepInstruction = this.prevStepInstruction,
        prevStepInstructionType = this.prevStepInstructionType
    )
}

fun NavigationInfoNavigationDomainModel.Arrived.toNavigationInfoPresentationModel(): NavigationInfoNavigationPresentationModel.Arrived {

    return NavigationInfoNavigationPresentationModel.Arrived(
        hasNextDestination = this.hasNextDestination,
        finalStepName = this.finalRouteStepName,
        finalStepInstruction = this.finalRouteStepInstruction,
        finalStepInstructionType = this.finalRouteStepInstructionType
    )
}
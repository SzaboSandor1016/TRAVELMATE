package com.example.features.findcustom.presentation.models

sealed interface CustomPlaceStatePresentationModel {

    data object Empty: CustomPlaceStatePresentationModel
    data class Custom(
        val canAddCustomPlace: Boolean = true,
        val place: CustomPlaceInfoPresentationModel
    ): CustomPlaceStatePresentationModel
}
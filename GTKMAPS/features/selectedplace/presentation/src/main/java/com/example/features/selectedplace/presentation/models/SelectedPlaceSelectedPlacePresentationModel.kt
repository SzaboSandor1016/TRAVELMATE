package com.example.features.selectedplace.presentation.models

sealed interface SelectedPlaceSelectedPlacePresentationModel {

    data object Default: SelectedPlaceSelectedPlacePresentationModel

    data class Selected(
        val uuid: String,
        val name: String?,
        val address: AddressSelectedPlacePresentationModel,
        val cuisine: String?,
        val openingHours: String?,
        val charge: String?,
    ): SelectedPlaceSelectedPlacePresentationModel
}
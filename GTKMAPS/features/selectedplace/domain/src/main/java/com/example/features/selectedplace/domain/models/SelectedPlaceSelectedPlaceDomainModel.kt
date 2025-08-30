package com.example.features.selectedplace.domain.models

sealed interface SelectedPlaceSelectedPlaceDomainModel {

    data object Default: SelectedPlaceSelectedPlaceDomainModel

    data class Selected(
        val uUID: String,
        val name: String?,
        val cuisine: String?,
        val openingHours: String?,
        val charge: String?,
        val address: AddressSelectedPlaceDomainModel,
        val coordinates: CoordinatesSelectedPlaceDomainModel,
        val category: String,
    ): SelectedPlaceSelectedPlaceDomainModel
}
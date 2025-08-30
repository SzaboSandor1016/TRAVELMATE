package com.example.features.selectedplace.domain.models

sealed interface SelectedPlaceInfoSelectedPlaceDomainModel {

    data object Default: SelectedPlaceInfoSelectedPlaceDomainModel

    data class Selected(
        val uUID: String,
        val name: String?,
        val cuisine: String?,
        val openingHours: String?,
        val charge: String?,
        val address: AddressSelectedPlaceDomainModel
    ): SelectedPlaceInfoSelectedPlaceDomainModel
}
package com.example.features.findcustom.domain.models

sealed interface PlaceCustomPlaceDomainModel{

    data object Default: PlaceCustomPlaceDomainModel

    data class CustomPlace(
        val uUID: String,
        val name: String?,
        val address: AddressCustomPlaceDomainModel,
        val coordinates: CoordinatesCustomPlaceDomainModel,
        val category: String
    ): PlaceCustomPlaceDomainModel
}
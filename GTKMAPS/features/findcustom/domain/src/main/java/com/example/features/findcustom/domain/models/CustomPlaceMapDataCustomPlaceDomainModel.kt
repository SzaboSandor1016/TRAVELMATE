package com.example.features.findcustom.domain.models

sealed interface CustomPlaceMapDataCustomPlaceDomainModel {

    data object Default: CustomPlaceMapDataCustomPlaceDomainModel

    data class CustomPlace(
        val uuid: String,
        val name: String?,
        val coordinates: CoordinatesCustomPlaceDomainModel,
        val category: String
    ):  CustomPlaceMapDataCustomPlaceDomainModel
}
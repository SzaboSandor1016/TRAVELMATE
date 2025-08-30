package com.example.features.findcustom.domain.models

sealed interface CustomPlaceInfoCustomPlaceDomainModel {

    data object Default: CustomPlaceInfoCustomPlaceDomainModel

    data class CustomPlace(
        val uUID: String? = null,
        val name: String? = null,
        val address: AddressCustomPlaceDomainModel
    ): CustomPlaceInfoCustomPlaceDomainModel
}
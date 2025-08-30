package com.example.features.selectedplace.presentation.models

data class AddressSelectedPlacePresentationModel(
    val city: String?,
    val street: String?,
    val houseNumber: String?,
    val country: String?
) {

    fun getAddressString(): String {

        val stringBuilder = StringBuilder()

        if(city != null) stringBuilder.append("$city ")
        if(street != null) stringBuilder.append("$street ")
        if (houseNumber != null) stringBuilder.append("$houseNumber ")
        if (country != null) stringBuilder.append(country)

        return stringBuilder.toString().trim()
    }
}
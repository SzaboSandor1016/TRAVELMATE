package com.example.features.findcustom.presentation.models

data class AddressCustomPlacePresentationModel(
    val city: String? = null,
    val street: String? = null,
    val houseNumber: String? = null,
    val country: String? = null
) {

    fun toAddressString(): String {

        val stringBuilder = StringBuilder("")

        if(this.city != null) stringBuilder.append(city)

        if (this.street != null) stringBuilder.append(" $street")

        if (this.houseNumber != null) stringBuilder.append(" $houseNumber")

        if (this.country != null) stringBuilder.append(" $country")

        return stringBuilder.toString()
    }
}
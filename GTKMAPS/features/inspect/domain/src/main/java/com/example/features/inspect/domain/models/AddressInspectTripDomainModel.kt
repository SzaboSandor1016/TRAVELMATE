package com.example.features.inspect.domain.models

data class AddressInspectTripDomainModel(
    val city: String? = null,
    val street: String? = null,
    val houseNumber: String? = null,
    val country: String? = null
) {
    fun getAddressInfo(): String {

        val stringBuilder = StringBuilder()

        if(city!=null) stringBuilder.append("$city ")

        if(street != null) stringBuilder.append("$street ")

        if (houseNumber != null) stringBuilder.append("$houseNumber ")

        if (country != null) stringBuilder.append(country)

        return stringBuilder.toString().trim()
    }
}
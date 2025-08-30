package com.example.features.inspect.domain.models

import java.util.UUID

data class PlaceInspectTripDomainModel(
    var uUID: String,
    var name: String?,
    var cuisine: String?,
    var openingHours: String?,
    var charge: String?,
    var address: AddressInspectTripDomainModel,
    var coordinates: CoordinatesInspectTripDomainModel,
    var category: String
) {
    fun getPlaceInfo(): String {

        val stringBuilder: StringBuilder = StringBuilder("")

        if (name!=null) stringBuilder.append(name)

        return stringBuilder.toString()
    }
    fun getPlaceAddressInfo(): String {

        return address.getAddressInfo()
    }
}
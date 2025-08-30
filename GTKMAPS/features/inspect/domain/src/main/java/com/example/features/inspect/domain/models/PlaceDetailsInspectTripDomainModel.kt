package com.example.features.inspect.domain.models

data class PlaceDetailsInspectTripDomainModel(
    val uuid: String,
    val name: String?,
    val addressInfo: String,
    val cuisine: String?,
    val openingHours: String?,
    val charge: String?
) {
}
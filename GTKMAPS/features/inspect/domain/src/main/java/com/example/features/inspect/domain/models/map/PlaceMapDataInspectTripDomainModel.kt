package com.example.features.inspect.domain.models.map

import com.example.features.inspect.domain.models.CoordinatesInspectTripDomainModel

data class PlaceMapDataInspectTripDomainModel(
    val uuid: String,
    val placeName: String?,
    val coordinates: CoordinatesInspectTripDomainModel,
    val category: String
) {
}
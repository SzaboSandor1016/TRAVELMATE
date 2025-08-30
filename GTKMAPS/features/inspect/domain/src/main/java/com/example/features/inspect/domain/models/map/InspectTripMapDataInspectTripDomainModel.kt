package com.example.features.inspect.domain.models.map

sealed interface InspectTripMapDataInspectTripDomainModel {

    data object Default: InspectTripMapDataInspectTripDomainModel

    data class Inspected(
        val uuid: String,
        val startMapData: PlaceMapDataInspectTripDomainModel,
        val daysMapData: List<DayOfTripMapDataInspectTripDomainModel>
    ): InspectTripMapDataInspectTripDomainModel
}
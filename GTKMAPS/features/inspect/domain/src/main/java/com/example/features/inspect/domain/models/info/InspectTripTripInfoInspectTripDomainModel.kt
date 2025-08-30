package com.example.features.inspect.domain.models.info

sealed interface InspectTripTripInfoInspectTripDomainModel {

    data object Default: InspectTripTripInfoInspectTripDomainModel

    data class Inspected(
        val uUID: String?,
        val title: String?,
        val startInfo: PlaceInfoInspectTripDomainModel,
        val daysInfo: List<DayOfTripInfoInspectTripDomainModel>,
        val creatorUsername: String?
    ): InspectTripTripInfoInspectTripDomainModel
}
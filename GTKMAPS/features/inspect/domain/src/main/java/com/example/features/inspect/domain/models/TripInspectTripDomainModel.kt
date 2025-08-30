package com.example.features.inspect.domain.models

import java.util.UUID

sealed interface TripInspectTripDomainModel{

    data object Default: TripInspectTripDomainModel

    data class Inspected(
        val uUID: String,
        val startPlace: PlaceInspectTripDomainModel,
        val days: List<DayOfTripInspectTripDomainModel>,
        val creatorUsername: String? = null,
        val date: String?,
        val title: String,
        val note: String?,
    ): TripInspectTripDomainModel
}
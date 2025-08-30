package com.example.features.inspect.presentation.models


sealed interface InspectTripInfoUIModel {

    data object Default: InspectTripInfoUIModel
    data class Inspected(
        val uuid: String?,
        val title: String?,
        val startInfo: PlaceInfoInspectTripPresentationModel,
        val daysInfo: List<DayOfTripInfoInspectTripPresentationModel>,
        val creatorUsername: String?
    ): InspectTripInfoUIModel
}
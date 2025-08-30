package com.example.travel_mate.ui.models

import org.osmdroid.views.overlay.Polyline

sealed interface MapDataMapPresentationModel {

    data class Search(
        val startPlace: PlaceDataMapPresentationModel? = null,
        val places: List<PlaceDataMapPresentationModel> = emptyList(),
        val parametersSelected: Boolean = false,
        val transportSelected: Boolean = false
    ): MapDataMapPresentationModel

    data class Inspect(
        val startPlace: PlaceDataMapPresentationModel,
        val days: List<DayOfTripMapPresentationModel>
    ): MapDataMapPresentationModel

    data class CustomPlace(
        val place: PlaceDataMapPresentationModel
    ): MapDataMapPresentationModel

    data class Route(
        val startPlace: PlaceDataMapPresentationModel,
        val polylines: List<Polyline>,
        val places: List<PlaceDataMapPresentationModel>
    ): MapDataMapPresentationModel

    data class Navigation(
        val goal: CoordinatesMapPresentationModel,
        val route: Polyline
    ): MapDataMapPresentationModel

    data object NavigationArrived: MapDataMapPresentationModel
}
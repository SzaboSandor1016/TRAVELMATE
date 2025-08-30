package com.example.features.trips.presentation.models

sealed interface TripTripsPresentationModel {

    data object Default: TripTripsPresentationModel {
    }

    data class Local(
        val uUID: String,
        val startPlace: PlaceTripsPresentationModel,
        val days: List<DayOfTripTripsPresentationModel>,
        /*private var creatorUID: String? = null
        private var contributors: Map<String, Boolean> = hashMapOf()*/
        val date: String?,
        val title: String,
        val note: String?,
    ): TripTripsPresentationModel {
    }

    data class Remote(
        val uUID: String,
        val startPlace: PlaceTripsPresentationModel,
        val days: List<DayOfTripTripsPresentationModel>,
        val date: String?,
        val title: String,
        val note: String?,
    ): TripTripsPresentationModel {
    }
}
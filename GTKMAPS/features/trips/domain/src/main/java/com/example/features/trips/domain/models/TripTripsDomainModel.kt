package com.example.features.trips.domain.models

sealed interface TripTripsDomainModel {

    data object Default: TripTripsDomainModel

    data class Local(
        var uUID: String,
        var startPlace: PlaceTripsDomainModel,
        var days: List<DayOfTripTripsDomainModel>,
        /*private var creatorUID: String? = null
        private var contributors: Map<String, Boolean> = hashMapOf()*/
        val date: String?,
        val title: String,
        val note: String?,
    ): TripTripsDomainModel

    data class Remote(
        val uUID: String,
        val startPlace: PlaceTripsDomainModel,
        val days: List<DayOfTripTripsDomainModel>,
        val date: String?,
        val title: String,
        val note: String?,
    ): TripTripsDomainModel
}
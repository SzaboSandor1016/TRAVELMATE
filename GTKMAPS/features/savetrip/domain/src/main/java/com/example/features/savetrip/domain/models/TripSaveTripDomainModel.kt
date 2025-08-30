package com.example.features.savetrip.domain.models

sealed interface TripSaveTripDomainModel {
    data object Default: TripSaveTripDomainModel
    data class Local(
        var uUID: String,
        var startPlace: PlaceSaveTripDomainModel,
        var places: List<PlaceSaveTripDomainModel>,
        /*private var creatorUID: String? = null
        private var contributors: Map<String, Boolean> = hashMapOf()*/
        var date: String?,
        var title: String,
        var note: String?,
    ): TripSaveTripDomainModel
    data class Remote(
        val uUID: String,
        val startPlace: PlaceSaveTripDomainModel,
        val places: List<PlaceSaveTripDomainModel>,
        var date: String?,
        var title: String,
        var note: String?,
    ): TripSaveTripDomainModel
}
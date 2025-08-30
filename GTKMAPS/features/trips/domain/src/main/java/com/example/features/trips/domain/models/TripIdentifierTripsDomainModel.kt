package com.example.features.trips.domain.models


sealed interface TripIdentifierTripsDomainModel {

    data class Local(
        val uuid: String,
        val title: String
    ) : TripIdentifierTripsDomainModel

    data class Remote(
        val uuid: String,
        //val location: String,
        val title: String,
        val contributorUIDs: Map<String, Boolean>,
        val contributors: Map<String, ContributorTripsDomainModel>,
        val permissionToUpdate: Boolean = true,
        val creatorUID: String,
        val creatorUsername: String = ""
    ): TripIdentifierTripsDomainModel

    data object Default: TripIdentifierTripsDomainModel
}
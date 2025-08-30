package com.example.features.savetrip.domain.models


sealed interface TripIdentifierSaveTripDomainModel {

    data class Local(
        val uuid: String,
        val title: String
    ) : TripIdentifierSaveTripDomainModel

    data class Remote(
        val uuid: String,
        //val location: String,
        val title: String,
        val contributorUIDs: Map<String, Boolean>,
        val contributors: Map<String, ContributorSaveTripDomainModel>,
        val permissionToUpdate: Boolean = true,
        val creatorUID: String,
        val creatorUsername: String = ""
    ): TripIdentifierSaveTripDomainModel

    data object Default: TripIdentifierSaveTripDomainModel
}
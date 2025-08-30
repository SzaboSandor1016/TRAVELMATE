package com.example.core.remotedatasources.tripremotedatasource.domain.models

data class TripIdentifierRemoteEntityModel(
    val uuid: String? = null,
    //val location: String,
    val title: String? = null,
    val contributorUIDs: Map<String, Boolean> = emptyMap(),
    val contributors: Map<String, ContributorRemoteEntityModel> = emptyMap(),
    val creatorUID: String? = null,
) {

}
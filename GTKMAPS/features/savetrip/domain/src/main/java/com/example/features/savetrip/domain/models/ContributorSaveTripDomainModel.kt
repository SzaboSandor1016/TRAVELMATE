package com.example.features.savetrip.domain.models

data class ContributorSaveTripDomainModel(
    val uid: String,
    val username: String,
    val canUpdate: Boolean = false,
    val selected: Boolean,
) {
}
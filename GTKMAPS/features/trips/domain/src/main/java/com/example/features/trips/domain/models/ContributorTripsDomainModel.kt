package com.example.features.trips.domain.models

data class ContributorTripsDomainModel(
    val uid: String,
    val username: String,
    val canUpdate: Boolean = false,
    val selected: Boolean,
) {
}
package com.example.features.trips.presentation.models

data class ContributorTripsPresentationModel(
    val uid: String,
    val username: String,
    val canUpdate: Boolean = false,
    val selected: Boolean,
) {
}
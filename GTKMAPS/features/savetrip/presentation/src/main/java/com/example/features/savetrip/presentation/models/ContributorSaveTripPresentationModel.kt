package com.example.features.savetrip.presentation.models

data class ContributorSaveTripPresentationModel(
    val uid: String,
    val username: String,
    val canUpdate: Boolean = false,
    val selected: Boolean,
) {
}
package com.example.features.inspect.domain.models

class ContributorInspectTripDomainModel(
    val uid: String,
    val username: String,
    val canUpdate: Boolean = false,
    val selected: Boolean,
) {
}
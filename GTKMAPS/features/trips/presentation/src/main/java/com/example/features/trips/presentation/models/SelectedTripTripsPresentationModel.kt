package com.example.features.trips.presentation.models

data class SelectedTripTripsPresentationModel(
    val selectedIdentifier: TripIdentifierTripsPresentationModel = TripIdentifierTripsPresentationModel.Default,
    val selectedTrip: TripTripsPresentationModel = TripTripsPresentationModel.Default
) {
}
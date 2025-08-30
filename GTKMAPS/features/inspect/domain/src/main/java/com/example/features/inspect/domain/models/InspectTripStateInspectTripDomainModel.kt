package com.example.features.inspect.domain.models

data class InspectTripStateInspectTripDomainModel(
    val inspectTripOptions: InspectTripOptionsInspectTripDomainModel = InspectTripOptionsInspectTripDomainModel(),
    val inspectedTrip: TripInspectTripDomainModel = TripInspectTripDomainModel.Default
) {
}
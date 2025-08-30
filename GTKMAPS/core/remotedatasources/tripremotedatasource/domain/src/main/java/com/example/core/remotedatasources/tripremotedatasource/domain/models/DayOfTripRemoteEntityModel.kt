package com.example.core.remotedatasources.tripremotedatasource.domain.models

data class DayOfTripRemoteEntityModel(
    val places: List<PlaceRemoteEntityModel> = emptyList(),
    val label: String
) {
    constructor(): this(
        label = "",
        places = emptyList()
    )
}
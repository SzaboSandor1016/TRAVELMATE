package com.example.core.remotedatasources.tripremotedatasource.domain.models

data class TripRemoteEntityModel(
    val uUID: String? = null,
    val startPlace: PlaceRemoteEntityModel? = null,
    val days: List<DayOfTripRemoteEntityModel> = emptyList(),
    val title: String,
    val date: String?,
    val note: String?,
) {
    constructor(): this(
        title = "",
        date = null,
        note = null
    )
}
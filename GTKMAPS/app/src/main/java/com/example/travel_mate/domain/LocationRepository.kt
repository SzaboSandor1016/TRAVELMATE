package com.example.travel_mate.domain

import android.location.Location

interface LocationRepository {

    fun startLocationUpdates()

    fun stopLocationUpdates()

    suspend fun getCurrentLocation(): Location?

    suspend fun updateCurrentLocation(): Location?
}
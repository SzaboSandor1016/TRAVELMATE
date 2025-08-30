package com.example.app.location.domain.repository

import android.location.Location

interface LocationRepository {

    fun startLocationUpdates()

    fun stopLocationUpdates()

    suspend fun getCurrentLocation(): Location?

    suspend fun updateCurrentLocation(): Location?
}
package com.example.app.location.domain.datasource

import android.location.Location

interface LocationLocalDataSource {

    suspend fun getCurrentLocation(): Location?

    fun startContinuousLocationUpdates()

    fun stopLocationUpdates()

    suspend fun updateCurrentLocation(): Location?
}
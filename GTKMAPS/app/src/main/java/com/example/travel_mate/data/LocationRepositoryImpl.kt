package com.example.travel_mate.data

import android.location.Location
import com.example.travel_mate.domain.LocationRepository

class LocationRepositoryImpl(
    private val locationLocalDataSource: LocationLocalDataSource
): LocationRepository {


    override fun startLocationUpdates() {
        locationLocalDataSource.startContinuousLocationUpdates()
    }

    override fun stopLocationUpdates() {
        locationLocalDataSource.stopLocationUpdates()
    }

    override suspend fun getCurrentLocation(): Location? {
        return locationLocalDataSource.getCurrentLocation()
    }

    override suspend fun updateCurrentLocation(): Location? {
        return locationLocalDataSource.updateCurrentLocation()
    }

}
package com.example.travel_mate.data

import android.location.Location
import com.example.travel_mate.domain.LocationRepository
import org.koin.java.KoinJavaComponent.inject

class LocationRepositoryImpl(
): LocationRepository {

    private val locationLocalDataSource: LocationLocalDataSource by inject(LocationLocalDataSource::class.java)

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
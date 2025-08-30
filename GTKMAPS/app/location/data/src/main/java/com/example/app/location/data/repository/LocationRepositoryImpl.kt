package com.example.app.location.data.repository

import android.location.Location
import com.example.app.location.data.datasource.LocationLocalDataSourceImpl
import com.example.app.location.domain.datasource.LocationLocalDataSource
import com.example.app.location.domain.repository.LocationRepository
import org.koin.java.KoinJavaComponent.inject

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
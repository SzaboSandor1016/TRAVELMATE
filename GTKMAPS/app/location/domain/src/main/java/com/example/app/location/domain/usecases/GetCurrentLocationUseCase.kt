package com.example.app.location.domain.usecases

import android.location.Location
import android.util.Log
import com.example.app.location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetCurrentLocationUseCase(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Flow<Location?> {

        val location = locationRepository.getCurrentLocation()

        Log.d("location", location?.latitude.toString() + " " + location?.longitude.toString())

        return flowOf(location)
    }
}
package com.example.travel_mate.domain

import android.location.Location
import com.example.travel_mate.data.Coordinates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetCurrentLocationUseCase(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Flow<Location?> {

        val location = locationRepository.getCurrentLocation()

        return flowOf(location)
    }
}
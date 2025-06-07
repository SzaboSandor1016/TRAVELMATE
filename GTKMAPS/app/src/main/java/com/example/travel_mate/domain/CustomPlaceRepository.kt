package com.example.travel_mate.domain

import com.example.travel_mate.data.CustomPlaceRepositoryImpl.CustomPlace
import com.example.travel_mate.data.Place
import kotlinx.coroutines.flow.StateFlow

interface CustomPlaceRepository {

    val customPlace: StateFlow<CustomPlace>

    suspend fun setCustomPlace(customPlace: Place)

    suspend fun resetCustomPlace()
}
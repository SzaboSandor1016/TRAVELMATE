package com.example.features.findcustom.domain.repositories

import com.example.features.findcustom.domain.models.CustomPlaceInfoCustomPlaceDomainModel
import com.example.features.findcustom.domain.models.CustomPlaceMapDataCustomPlaceDomainModel
import com.example.features.findcustom.domain.models.CustomPlaceStateCustomPlaceModel
import com.example.features.findcustom.domain.models.PlaceCustomPlaceDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CustomPlaceRepository {

    val customPlace: StateFlow<CustomPlaceStateCustomPlaceModel>

    fun getCustomPlaceInfo(): Flow<CustomPlaceInfoCustomPlaceDomainModel>

    fun getCustomPlaceMapData(): Flow<CustomPlaceMapDataCustomPlaceDomainModel>

    suspend fun getCustomPlace(): PlaceCustomPlaceDomainModel

    suspend fun setCustomPlace(customPlace: PlaceCustomPlaceDomainModel.CustomPlace)

    suspend fun resetCustomPlace()
}
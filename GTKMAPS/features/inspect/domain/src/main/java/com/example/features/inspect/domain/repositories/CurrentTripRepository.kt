package com.example.features.inspect.domain.repositories

import com.example.features.inspect.domain.models.PlaceDetailsInspectTripDomainModel
import com.example.features.inspect.domain.models.map.InspectTripMapDataInspectTripDomainModel
import com.example.features.inspect.domain.models.info.InspectTripTripInfoInspectTripDomainModel
import com.example.features.inspect.domain.models.PlaceInspectTripDomainModel
import com.example.features.inspect.domain.models.TripInspectTripDomainModel
import kotlinx.coroutines.flow.Flow

interface CurrentTripRepository {

    fun getCurrentTripInfo(): Flow<InspectTripTripInfoInspectTripDomainModel>

    fun getCurrentTripMapData(): Flow<InspectTripMapDataInspectTripDomainModel>

    fun getInspectedTripPlaceDetails(placeUUID: String): PlaceDetailsInspectTripDomainModel?

    suspend fun setSelectedInspectedDay(position: Int)

    fun setCurrentTrip(currentTrip: TripInspectTripDomainModel.Inspected)

    fun resetCurrentTrip()

    fun getPlaceByUUID(placeUUID: String): PlaceInspectTripDomainModel?
}
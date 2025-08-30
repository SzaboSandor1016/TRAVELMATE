package com.example.features.inspect.data.repositories

import android.util.Log
import com.example.features.inspect.data.mappers.toFlowOfInspectTripMapData
import com.example.features.inspect.data.mappers.toFlowOfInspectedTripInfo
import com.example.features.inspect.data.mappers.toPlaceDetailsDomainModel
import com.example.features.inspect.domain.models.InspectTripOptionsInspectTripDomainModel
import com.example.features.inspect.domain.models.map.InspectTripMapDataInspectTripDomainModel
import com.example.features.inspect.domain.models.InspectTripStateInspectTripDomainModel
import com.example.features.inspect.domain.models.PlaceDetailsInspectTripDomainModel
import com.example.features.inspect.domain.models.info.InspectTripTripInfoInspectTripDomainModel
import com.example.features.inspect.domain.models.PlaceInspectTripDomainModel
import com.example.features.inspect.domain.models.TripInspectTripDomainModel
import com.example.features.inspect.domain.repositories.CurrentTripRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class CurrentTripRepositoryImpl: CurrentTripRepository {

    private val _currentTripState = MutableStateFlow(InspectTripStateInspectTripDomainModel())

    private val currentTripCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun getCurrentTripInfo(): Flow<InspectTripTripInfoInspectTripDomainModel> {

        return _currentTripState.toFlowOfInspectedTripInfo()
    }

    override fun getCurrentTripMapData(): Flow<InspectTripMapDataInspectTripDomainModel> {

        return _currentTripState.toFlowOfInspectTripMapData()
    }

    override fun getInspectedTripPlaceDetails(placeUUID: String): PlaceDetailsInspectTripDomainModel? {

        return (_currentTripState.value.inspectedTrip as TripInspectTripDomainModel.Inspected).days
            .flatMap { it.places }.find { it.uUID==placeUUID }?.toPlaceDetailsDomainModel()
    }

    override suspend fun setSelectedInspectedDay(position: Int) {

        withContext(currentTripCoroutineDispatcher) {

            _currentTripState.update {

                it.copy(

                    inspectTripOptions = it.inspectTripOptions.copy(

                        currentSelectedDayPosition = position
                    )
                )
            }
        }


    }


    override fun setCurrentTrip(currentTrip: TripInspectTripDomainModel.Inspected) {

        _currentTripState.update {

            it.copy(
                inspectedTrip = currentTrip,
                inspectTripOptions = InspectTripOptionsInspectTripDomainModel()
            )
        }

        Log.d("inspectedTripPlaces", "Number of places" +
                "\n ${(_currentTripState.value.inspectedTrip as TripInspectTripDomainModel.Inspected).days.flatMap { it.places }.size}")
    }

    override fun resetCurrentTrip() {
        _currentTripState.update {

            it.copy(
                inspectedTrip = TripInspectTripDomainModel.Default
            )
        }
    }

    override fun getPlaceByUUID(placeUUID: String): PlaceInspectTripDomainModel? {

        return when(_currentTripState.value.inspectedTrip) {

            is TripInspectTripDomainModel.Default -> null

            is TripInspectTripDomainModel.Inspected -> {
                (_currentTripState.value.inspectedTrip as TripInspectTripDomainModel.Inspected).days.flatMap {
                    it.places
                }.find { it.uUID == placeUUID }
            }
        }
    }
}
package com.example.features.findcustom.data.repositories

import com.example.features.findcustom.domain.mappers.mapToInfoState
import com.example.features.findcustom.domain.mappers.mapToMapData
import com.example.features.findcustom.domain.models.CustomPlaceInfoCustomPlaceDomainModel
import com.example.features.findcustom.domain.models.CustomPlaceMapDataCustomPlaceDomainModel
import com.example.features.findcustom.domain.models.CustomPlaceStateCustomPlaceModel
import com.example.features.findcustom.domain.models.PlaceCustomPlaceDomainModel
import com.example.features.findcustom.domain.repositories.CustomPlaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class CustomPlaceRepositoryImpl: CustomPlaceRepository {

    private val customPlaceCoroutineDispatcher = Dispatchers.IO

    private val _customPlaceState = MutableStateFlow(CustomPlaceStateCustomPlaceModel())
    override val customPlace: StateFlow<CustomPlaceStateCustomPlaceModel> = _customPlaceState.asStateFlow()


    override fun getCustomPlaceInfo(): Flow<CustomPlaceInfoCustomPlaceDomainModel> {
        return _customPlaceState.mapToInfoState()
    }

    override fun getCustomPlaceMapData(): Flow<CustomPlaceMapDataCustomPlaceDomainModel> {
        return _customPlaceState.mapToMapData()
    }

    override suspend fun getCustomPlace(): PlaceCustomPlaceDomainModel {
        return _customPlaceState.value.customPlace
    }


    override suspend fun setCustomPlace(customPlace: PlaceCustomPlaceDomainModel.CustomPlace) {

        withContext(customPlaceCoroutineDispatcher) {

            _customPlaceState.update {

                it.copy(
                    customPlace = customPlace
                )
            }
        }
    }

    override suspend fun resetCustomPlace() {

        withContext(customPlaceCoroutineDispatcher) {

            _customPlaceState.update {

                it.copy(
                    customPlace = PlaceCustomPlaceDomainModel.Default
                )
            }
        }
    }
}
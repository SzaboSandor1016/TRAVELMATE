package com.example.travel_mate.data

import com.example.travel_mate.domain.CustomPlaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class CustomPlaceRepositoryImpl(
    private val photonRemoteDataSource: PhotonRemoteDataSource
): CustomPlaceRepository {

    private val customPlaceCoroutineDispatcher = Dispatchers.IO

    private val _customPlaceState = MutableStateFlow(CustomPlace())
    override val customPlace: StateFlow<CustomPlace> = _customPlaceState.asStateFlow()

    override suspend fun setCustomPlace(customPlace: Place) {

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
                    customPlace = null
                )
            }
        }
    }

    data class CustomPlace(
        val customPlace: Place? = null
    )
}
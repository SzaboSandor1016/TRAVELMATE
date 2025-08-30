package com.example.features.selectedplace.domain.repository

import com.example.features.selectedplace.domain.models.SelectedPlaceInfoSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.models.SelectedPlaceSelectedPlaceDomainModel
import kotlinx.coroutines.flow.Flow

interface SelectedPlaceRepository {

    fun getSelectedPlaceInfo(): Flow<SelectedPlaceInfoSelectedPlaceDomainModel>

    fun getFullSelectedPlaceData(): SelectedPlaceSelectedPlaceDomainModel.Selected

    fun setSelectedPlace(selectedPlace: SelectedPlaceSelectedPlaceDomainModel.Selected)

    fun resetSelectedPlace()
}
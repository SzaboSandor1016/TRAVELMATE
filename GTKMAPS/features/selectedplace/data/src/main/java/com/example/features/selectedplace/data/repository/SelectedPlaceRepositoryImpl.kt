package com.example.features.selectedplace.data.repository

import com.example.features.selectedplace.data.mappers.toFlowOfSelectedPlaceInfo
import com.example.features.selectedplace.domain.models.SelectedPlaceInfoSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.models.SelectedPlaceSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.repository.SelectedPlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SelectedPlaceRepositoryImpl: SelectedPlaceRepository {

    private val _selectedPlaceState: MutableStateFlow<SelectedPlaceSelectedPlaceDomainModel> = MutableStateFlow(
        SelectedPlaceSelectedPlaceDomainModel.Default)

    override fun getSelectedPlaceInfo(): Flow<SelectedPlaceInfoSelectedPlaceDomainModel> {
        return _selectedPlaceState.toFlowOfSelectedPlaceInfo()
    }

    override fun getFullSelectedPlaceData(): SelectedPlaceSelectedPlaceDomainModel.Selected {

        return _selectedPlaceState.value as SelectedPlaceSelectedPlaceDomainModel.Selected
    }

    override fun setSelectedPlace(selectedPlace: SelectedPlaceSelectedPlaceDomainModel.Selected) {

        _selectedPlaceState.update {

            selectedPlace
        }
    }

    override fun resetSelectedPlace() {

        _selectedPlaceState.update {
            SelectedPlaceSelectedPlaceDomainModel.Default
        }
    }
}
package com.example.features.selectedplace.data.mappers

import com.example.features.selectedplace.domain.models.SelectedPlaceInfoSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.models.SelectedPlaceSelectedPlaceDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

fun StateFlow<SelectedPlaceSelectedPlaceDomainModel>.toFlowOfSelectedPlaceInfo(): Flow<SelectedPlaceInfoSelectedPlaceDomainModel> {

    return this.map { selectedPlace ->

        when(selectedPlace) {

            is SelectedPlaceSelectedPlaceDomainModel.Default -> SelectedPlaceInfoSelectedPlaceDomainModel.Default
            is SelectedPlaceSelectedPlaceDomainModel.Selected -> SelectedPlaceInfoSelectedPlaceDomainModel.Selected(
                uUID = selectedPlace.uUID,
                name = selectedPlace.name,
                cuisine = selectedPlace.cuisine,
                openingHours = selectedPlace.openingHours,
                charge = selectedPlace.charge,
                address = selectedPlace.address
            )
        }
    }
}
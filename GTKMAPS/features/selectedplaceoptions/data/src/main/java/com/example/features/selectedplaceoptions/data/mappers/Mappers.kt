package com.example.features.selectedplaceoptions.data.mappers

import com.example.features.selectedplaceoptions.domain.models.MainSelectedPlaceOptions
import com.example.features.selectedplaceoptions.domain.models.SelectedPlaceSelectedPlaceOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

fun StateFlow<MainSelectedPlaceOptions>.toFlowOfMainSelectedPlaceOptions(): Flow<MainSelectedPlaceOptions> {

    return this.map {

        it
    }
}

fun StateFlow<SelectedPlaceSelectedPlaceOptions>.toFlowOfSelectedPlaceOptions(): Flow<SelectedPlaceSelectedPlaceOptions> {

    return this.map {

        it
    }
}
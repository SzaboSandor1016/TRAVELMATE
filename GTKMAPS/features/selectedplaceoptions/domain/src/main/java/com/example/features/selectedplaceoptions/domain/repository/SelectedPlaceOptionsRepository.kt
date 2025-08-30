package com.example.features.selectedplaceoptions.domain.repository

import com.example.features.selectedplaceoptions.domain.models.MainSelectedPlaceOptions
import com.example.features.selectedplaceoptions.domain.models.SelectedPlaceSelectedPlaceOptions
import kotlinx.coroutines.flow.Flow

interface SelectedPlaceOptionsRepository {

    fun setMainContainerHeight(height: Int)

    fun getMainOptionsAsFlow(): Flow<MainSelectedPlaceOptions>

    fun setOriginOfSelectedPlace(origin: String)

    fun setStateOfSelectedPlaceContainer(state: String)

    fun getSelectedPlaceOptionsAsFlow(): Flow<SelectedPlaceSelectedPlaceOptions>
}
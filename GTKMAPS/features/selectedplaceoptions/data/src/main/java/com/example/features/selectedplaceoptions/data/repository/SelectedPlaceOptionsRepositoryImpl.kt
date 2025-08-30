package com.example.features.selectedplaceoptions.data.repository

import com.example.features.selectedplaceoptions.data.mappers.toFlowOfMainSelectedPlaceOptions
import com.example.features.selectedplaceoptions.data.mappers.toFlowOfSelectedPlaceOptions
import com.example.features.selectedplaceoptions.domain.models.MainSelectedPlaceOptions
import com.example.features.selectedplaceoptions.domain.models.SelectedPlaceSelectedPlaceOptions
import com.example.features.selectedplaceoptions.domain.repository.SelectedPlaceOptionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SelectedPlaceOptionsRepositoryImpl: SelectedPlaceOptionsRepository {

    private val _mainOptionsState = MutableStateFlow(MainSelectedPlaceOptions())
    private val _selectedPlaceOptionsState = MutableStateFlow(SelectedPlaceSelectedPlaceOptions())

    override fun setMainContainerHeight(height: Int) {

        _mainOptionsState.update {
            it.copy(
                containerHeight = height
            )
        }
    }

    override fun getMainOptionsAsFlow(): Flow<MainSelectedPlaceOptions> {

        return _mainOptionsState.toFlowOfMainSelectedPlaceOptions()
    }

    override fun setOriginOfSelectedPlace(origin: String) {

        _selectedPlaceOptionsState.update {
            it.copy(
                origin = origin
            )
        }
    }

    override fun setStateOfSelectedPlaceContainer(state: String) {

        _selectedPlaceOptionsState.update {
            it.copy(
                containerState = state
            )
        }
    }

    override fun getSelectedPlaceOptionsAsFlow(): Flow<SelectedPlaceSelectedPlaceOptions> {

        return _selectedPlaceOptionsState.toFlowOfSelectedPlaceOptions()
    }
}
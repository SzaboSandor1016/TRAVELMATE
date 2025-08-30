package com.example.features.inspect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.features.inspect.domain.usecases.GetInspectedTripInfoUseCase
import com.example.features.inspect.domain.usecases.ResetInspectTripUseCase
import com.example.features.inspect.domain.usecases.SetSelectedDayOfInspectedTripUseCase
import com.example.features.inspect.presentation.mappers.mapToInspectTripInfoPresentationModel
import com.example.features.inspect.presentation.models.InspectTripStatePresentationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InspectTripViewModel(
    private val getInspectedTripInfoUseCase: GetInspectedTripInfoUseCase,
    private val resetInspectedTripUseCase: ResetInspectTripUseCase,
    private val setSelectedDayOfInspectedTripUseCase: SetSelectedDayOfInspectedTripUseCase
): ViewModel() {


    val inspectTripInfoState: StateFlow<InspectTripStatePresentationModel> by lazy {

        getInspectedTripInfoUseCase().map{ inspectTripInfo ->

            InspectTripStatePresentationModel(
                inspectTripInfo.mapToInspectTripInfoPresentationModel()
            )

        }.flowOn(
            Dispatchers.Main
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            InspectTripStatePresentationModel()
        )
    }

    fun setSelectedDayOfInspectedTrip(index: Int) {

        viewModelScope.launch {

            setSelectedDayOfInspectedTripUseCase(
                index = index
            )
        }
    }

    fun endInspecting() {

        resetInspectedTripUseCase()
    }
}
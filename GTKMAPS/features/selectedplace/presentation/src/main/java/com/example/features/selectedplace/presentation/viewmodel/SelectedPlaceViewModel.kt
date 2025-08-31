package com.example.features.selectedplace.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.features.route.domain.usecases.AddRemovePlaceToRouteUseCase
import com.example.features.route.domain.usecases.IsPlaceContainedByRouteUseCase
import com.example.features.savetrip.domain.usecases.AddRemoveAssignablePlaceUseCase
import com.example.features.savetrip.domain.usecases.IsContainedByTripUseCase
import com.example.features.selectedplace.domain.models.SelectedPlaceInfoSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.usecases.GetSelectedPlaceFullDataUseCase
import com.example.features.selectedplace.domain.usecases.GetSelectedPlaceInfoUseCase
import com.example.features.selectedplace.domain.usecases.ResetSelectedPlaceUseCase
import com.example.features.selectedplace.presentation.mappers.toPlaceRouteDomainModel
import com.example.features.selectedplace.presentation.mappers.toPlaceSaveTripDomainModel
import com.example.features.selectedplace.presentation.mappers.toSelectedPlaceOptionsPresentationModel
import com.example.features.selectedplace.presentation.mappers.toSelectedPlacePresentationModel
import com.example.features.selectedplace.presentation.models.SelectedPlaceOptionsPresentationModel
import com.example.features.selectedplace.presentation.models.SelectedPlaceSelectedPlacePresentationModel
import com.example.features.selectedplace.presentation.models.TripRouteButtonsState
import com.example.features.selectedplaceoptions.domain.usecases.GetSelectedPlaceSelectedPlaceOptionsAsFlowUseCase
import com.example.features.selectedplaceoptions.domain.usecases.SetMainContainerHeightUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
data class SelectedPlaceViewModel(
    private val getSelectedPlaceInfoUseCase: GetSelectedPlaceInfoUseCase,
    private val getSelectedPlaceFullDataUseCase: GetSelectedPlaceFullDataUseCase,
    private val resetSelectedPlaceUseCase: ResetSelectedPlaceUseCase,
    private val addRemoveAssignablePlaceUseCase: AddRemoveAssignablePlaceUseCase,
    private val addRemovePlaceToRouteUseCase: AddRemovePlaceToRouteUseCase,
    private val isPlaceContainedByRouteUseCase: IsPlaceContainedByRouteUseCase,
    private val isPlaceContainedByTripUseCase: IsContainedByTripUseCase,
    private val getSelectedPlaceSelectedPlaceOptionsAsFlowUseCase: GetSelectedPlaceSelectedPlaceOptionsAsFlowUseCase,
    private val setMainContainerHeightUseCase: SetMainContainerHeightUseCase,

    ): ViewModel() {

    val selectedPlaceOptions: StateFlow<SelectedPlaceOptionsPresentationModel> by lazy {

        getSelectedPlaceSelectedPlaceOptionsAsFlowUseCase().map {

            it.toSelectedPlaceOptionsPresentationModel()
        }.flowOn(
            Dispatchers.Main
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedPlaceOptionsPresentationModel(
                origin = "search",
                containerState = "expanded"
            )
        )
    }

    private val _tripRouteState: MutableStateFlow<TripRouteButtonsState> = MutableStateFlow(
        TripRouteButtonsState())

    val tripRouteState: StateFlow<TripRouteButtonsState> = _tripRouteState.asStateFlow()

    private val _selectedPlaceState: MutableStateFlow<SelectedPlaceSelectedPlacePresentationModel> =
        MutableStateFlow(SelectedPlaceSelectedPlacePresentationModel.Default)
    val selectedPlaceState: StateFlow<SelectedPlaceSelectedPlacePresentationModel> = _selectedPlaceState.asStateFlow()

    init {

        viewModelScope.launch {

            combine(

                getSelectedPlaceInfoUseCase(),
                _selectedPlaceState
            ) { selectedPlace, placeState ->

                when (selectedPlace) {

                    is SelectedPlaceInfoSelectedPlaceDomainModel.Default -> SelectedPlaceSelectedPlacePresentationModel.Default

                    is SelectedPlaceInfoSelectedPlaceDomainModel.Selected -> selectedPlace.toSelectedPlacePresentationModel()
                }
            }.collect { newState ->

                _selectedPlaceState.value = newState
            }
        }

        viewModelScope.launch {
            _selectedPlaceState
                .filterIsInstance<SelectedPlaceSelectedPlacePresentationModel.Selected>().flatMapLatest { selected ->

                    combine(
                        isPlaceContainedByRouteUseCase(selected.uuid),
                        _tripRouteState
                    ) { isContained, tripRouteState ->

                        Log.d("routeButtonState", "${selected.uuid} is ContainedBy route: $isContained")

                        TripRouteButtonsState(
                            isContained,
                            tripRouteState.containedByTrip
                        )

                    }
                }.collectLatest { newState ->

                    _tripRouteState.value = newState
                }
        }
        viewModelScope.launch {
            _selectedPlaceState.filterIsInstance<SelectedPlaceSelectedPlacePresentationModel.Selected>()
                .flatMapLatest { selected ->
                    // For each new selected place, start combining the flows
                    combine(
                        isPlaceContainedByTripUseCase(selected.uuid), _tripRouteState
                    ) { isContained, tripRouteState ->

                        TripRouteButtonsState(
                            tripRouteState.containedByRoute, isContained
                        )
                    }
                }.collectLatest { newState ->

                    _tripRouteState.value = newState
                }
        }
    }

    fun addPlaceToTrip() {

        viewModelScope.launch {

            val fullPlaceData = getSelectedPlaceFullDataUseCase()

            addRemoveAssignablePlaceUseCase(
                place = fullPlaceData.toPlaceSaveTripDomainModel()
            )
        }
    }

    fun addPlaceToRoute() {

        viewModelScope.launch {

            val fullPlaceData = getSelectedPlaceFullDataUseCase()

            addRemovePlaceToRouteUseCase(
                place = fullPlaceData.toPlaceRouteDomainModel()
            )
        }
    }

    fun resetSelectedPlace() {

        viewModelScope.launch {

            resetSelectedPlaceUseCase()
        }
    }

    fun setMainContainerHeight(height: Int) {
        
        viewModelScope.launch { 
            
            setMainContainerHeightUseCase(
                height = height
            )
        }
    }
}
package com.example.features.trips.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.features.trips.domain.usecases.DeleteTripUseCase
import com.example.features.trips.domain.usecases.GetLocalTripsUseCase
import com.example.features.trips.domain.usecases.GetRemoteContributedTripsUseCase
import com.example.features.trips.domain.usecases.GetRemoteTripsUseCase
import com.example.features.trips.domain.usecases.GetSelectedTripDataUseCase
import com.example.features.inspect.domain.usecases.SetInspectedTripUseCase
import com.example.features.savetrip.domain.usecases.InitSaveBySelectingTripToUpdateUseCase
import com.example.features.trips.presentation.mappers.createSaveTripDataSaveTripDomainModelFrom
import com.example.features.trips.presentation.mappers.toInspectTripDomainModel
import com.example.features.trips.presentation.mappers.toTripIdentifierDomainModel
import com.example.features.trips.presentation.mappers.toTripIdentifierPresentationModel
import com.example.features.trips.presentation.mappers.toTripPresentationModel
import com.example.features.trips.presentation.models.SelectedTripTripsPresentationModel
import com.example.features.trips.presentation.models.TripIdentifierTripsPresentationModel
import com.example.features.trips.presentation.models.TripTripsPresentationModel
import com.example.features.trips.presentation.models.TripsStateTripsPresentationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TripsViewModel(
    private val getLocalTripsUseCase: GetLocalTripsUseCase,
    private val getRemoteTripsUseCase: GetRemoteTripsUseCase,
    private val getRemoteContributedTripsUseCase: GetRemoteContributedTripsUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
    private val getSelectedTripDataUseCase: GetSelectedTripDataUseCase,
    private val setInspectedTripUseCase: SetInspectedTripUseCase,
    private val initSaveBySelectingTripToUpdateUseCase: InitSaveBySelectingTripToUpdateUseCase
): ViewModel() {

    //TODO create the state flow of the currently selected trip
    // then when selecting one set the identifier part as the selected then
    // update the ^ state flow with the returned data from getSelectedTripDataUseCase

    //TODO Separate local and remote save by replacing the current if contributors.isEmpty()
    // with dedicated share button where in addition to the title the user can add contributors a date a note
    // to the trip

    //TODO *Rough plan - needs to be refined*
    // plus to the trip sharing saving features add the organize places into programs for days.


    private val _tripsState = MutableStateFlow(TripsStateTripsPresentationModel())
    val tripsState: StateFlow<TripsStateTripsPresentationModel> = _tripsState.asStateFlow()

    private val _selectedTripState = MutableStateFlow(SelectedTripTripsPresentationModel())
    val selectedTripState: StateFlow<SelectedTripTripsPresentationModel> = _selectedTripState.asStateFlow()


    fun getTripsBySelectedTabLayoutItemIndex(index: Int) {

        viewModelScope.launch {

            val trips = when(index) {
                0 -> getLocalTripsUseCase()
                1 -> getRemoteTripsUseCase()
                else -> getRemoteContributedTripsUseCase()
            }

            trips.collect { collected ->

                _tripsState.update {
                    it.copy(
                        collected.map {
                            it.toTripIdentifierPresentationModel()
                        }
                    )
                }
            }
        }
    }

    fun deleteSelectedTrip() {

        viewModelScope.launch {
            deleteTripUseCase(
                tripIdentifier = _selectedTripState.value.selectedIdentifier.toTripIdentifierDomainModel()
            )
        }
    }

    fun getSelectedTripData(tripIdentifier: TripIdentifierTripsPresentationModel) {

        viewModelScope.launch {

            getSelectedTripDataUseCase(tripIdentifier.toTripIdentifierDomainModel()).collect{ tripData ->

                _selectedTripState.update {
                    it.copy(
                        selectedIdentifier = tripIdentifier,
                        selectedTrip = tripData.toTripPresentationModel()
                    )
                }
            }
        }
    }

    fun setInspectedTrip() {

        viewModelScope.launch {

            when(_selectedTripState.value.selectedTrip) {
                TripTripsPresentationModel.Default -> {}
                is TripTripsPresentationModel.Local -> setInspectedTripUseCase(
                    (_selectedTripState.value.selectedTrip as TripTripsPresentationModel.Local).toInspectTripDomainModel()
                )
                is TripTripsPresentationModel.Remote -> setInspectedTripUseCase(
                    (_selectedTripState.value.selectedTrip as TripTripsPresentationModel.Remote).toInspectTripDomainModel(
                        (_selectedTripState.value.selectedIdentifier as TripIdentifierTripsPresentationModel.Remote).creatorUsername
                    )
                )
            }
        }
    }

    fun updateSelectedTrip() {

        viewModelScope.launch {

            Log.d("updateSaveTripsViewModel", "executed")

            if (_selectedTripState.value.selectedIdentifier is TripIdentifierTripsPresentationModel.Default) {
                Log.d("SelectedTrip", "Selected trip is default")
            }

            when(_selectedTripState.value.selectedTrip) {
                    TripTripsPresentationModel.Default -> {}
                    is TripTripsPresentationModel.Local -> initSaveBySelectingTripToUpdateUseCase(createSaveTripDataSaveTripDomainModelFrom(
                        trip = (_selectedTripState.value.selectedTrip as TripTripsPresentationModel.Local),
                        tripIdentifier = (_selectedTripState.value.selectedIdentifier as TripIdentifierTripsPresentationModel.Local)
                    ))
                    is TripTripsPresentationModel.Remote -> initSaveBySelectingTripToUpdateUseCase(createSaveTripDataSaveTripDomainModelFrom(
                        trip = (_selectedTripState.value.selectedTrip as TripTripsPresentationModel.Remote),
                        tripIdentifier = (_selectedTripState.value.selectedIdentifier as TripIdentifierTripsPresentationModel.Remote)
                    ))
            }
        }
    }
}
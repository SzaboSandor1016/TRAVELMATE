package com.example.features.findcustom.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.features.findcustom.domain.models.CustomPlaceInfoCustomPlaceDomainModel
import com.example.features.findcustom.domain.models.PlaceCustomPlaceDomainModel
import com.example.features.findcustom.domain.usecases.GetCustomPlaceFullDataUseCase
import com.example.features.findcustom.domain.usecases.GetCustomPlaceInfoUseCase
import com.example.features.navigation.domain.usecases.NavigateToCustomPlaceUseCase
import com.example.features.findcustom.domain.usecases.ResetCustomPlaceUseCase
import com.example.features.search.domain.usecases.InitSearchWithSelectedStartUseCase
import com.example.features.findcustom.presentation.mappers.toCustomPlacePresentationModel
import com.example.features.findcustom.presentation.mappers.toCustomPlaceUiModel
import com.example.features.findcustom.presentation.mappers.toPlaceSearchDomainModel
import com.example.features.findcustom.presentation.models.CustomPlaceStatePresentationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CustomPlaceViewModel(
    private val getCustomPlaceInfoUseCase: GetCustomPlaceInfoUseCase,
    private val getCustomPlaceFullDataUseCase: GetCustomPlaceFullDataUseCase,
    private val resetCustomPlaceUseCase: ResetCustomPlaceUseCase,
    private val initSearchWithSelectedStartUseCase: InitSearchWithSelectedStartUseCase,
    private val navigateToCustomPlaceUseCase: NavigateToCustomPlaceUseCase
): ViewModel() {

    val infoState: StateFlow<CustomPlaceStatePresentationModel> by lazy {

        getCustomPlaceInfoUseCase().map { customPlaceInfo ->

            when(customPlaceInfo) {
                is CustomPlaceInfoCustomPlaceDomainModel.CustomPlace -> CustomPlaceStatePresentationModel.Custom(
                    place = customPlaceInfo.toCustomPlaceUiModel()
                )
                is CustomPlaceInfoCustomPlaceDomainModel.Default -> CustomPlaceStatePresentationModel.Empty
            }
        }.flowOn(
            Dispatchers.Main
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            CustomPlaceStatePresentationModel.Empty
        )
    }

    fun setAsStartPlaceOfSearch() {

        viewModelScope.launch {
            val currentCustomPlace = (getCustomPlaceFullDataUseCase() as PlaceCustomPlaceDomainModel.CustomPlace).toCustomPlacePresentationModel()

            resetCustomPlaceUseCase()

            initSearchWithSelectedStartUseCase(
                startPlace = currentCustomPlace.toPlaceSearchDomainModel()
            )
        }
    }

    fun resetCustomPlace() {

        viewModelScope.launch {

            resetCustomPlaceUseCase()
        }
    }

    fun navigateToCustomPlace(transportMode: String) {

        viewModelScope.launch {

            val currentCustomPlace = (getCustomPlaceFullDataUseCase() as PlaceCustomPlaceDomainModel.CustomPlace).toCustomPlacePresentationModel()

            navigateToCustomPlaceUseCase(
                latitude = currentCustomPlace.coordinates.latitude,
                longitude = currentCustomPlace.coordinates.longitude,
                transportMode = transportMode
            )
        }
    }
}
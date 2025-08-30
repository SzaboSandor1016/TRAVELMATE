package com.example.features.route.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.features.inspect.domain.usecases.FindPlaceByUUIDInInspectUseCase
import com.example.features.navigation.domain.usecases.InitNavigationUseCase
import com.example.features.navigation.domain.usecases.NavigateToPlaceInRouteUseCase
import com.example.features.route.domain.usecases.GetRouteInfoUseCase
import com.example.features.route.domain.usecases.GetRouteTransportModeUseCase
import com.example.features.route.domain.usecases.OptimizeRouteUseCase
import com.example.features.route.domain.usecases.RemovePlaceFromRouteUseCase
import com.example.features.route.domain.usecases.ReorderRouteUseCase
import com.example.features.route.domain.usecases.ResetRouteUseCase
import com.example.features.route.domain.usecases.SetRouteTransportModeUseCase
import com.example.features.route.presentation.mappers.toPlaceRoutePresentationModel
import com.example.features.route.presentation.mappers.toPlaceSelectedPlaceDomainModel
import com.example.features.route.presentation.mappers.toRouteInfoPresentationModel
import com.example.features.route.presentation.models.RouteInfoStateRoutePresentationModel
import com.example.features.search.domain.usecases.FindPlaceByUUIDInSearchUseCase
import com.example.features.selectedplace.domain.usecases.ResetSelectedPlaceUseCase
import com.example.features.selectedplace.domain.usecases.SetSelectedPlaceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RouteViewModel(
    private val getRouteInfoUseCase: GetRouteInfoUseCase,
    private val resetRouteUseCase: ResetRouteUseCase,
    private val removePlaceFromRouteUseCase: RemovePlaceFromRouteUseCase,
    private val reorderRouteUseCase: ReorderRouteUseCase,
    private val optimizeRouteUseCase: OptimizeRouteUseCase,
    private val initNavigationUseCase: InitNavigationUseCase,
    private val navigateToPlaceInRouteUseCase: NavigateToPlaceInRouteUseCase,
    private val findPlaceByUUIDInInspectUseCase: FindPlaceByUUIDInInspectUseCase,
    private val findPlaceByUUIDInSearchUseCase: FindPlaceByUUIDInSearchUseCase,
    private val setSelectedPlaceUseCase: SetSelectedPlaceUseCase,
    private val resetSelectedPlaceUseCase: ResetSelectedPlaceUseCase,
    private val setRouteTransportModeUseCase: SetRouteTransportModeUseCase,
    private val getRouteTransportModeUseCase: GetRouteTransportModeUseCase
): ViewModel() {

    val routeInfoState: StateFlow<RouteInfoStateRoutePresentationModel> by lazy {
        getRouteInfoUseCase().map { routeInfoState ->

            if (routeInfoState.infoNodes.size > 1) {
                RouteInfoStateRoutePresentationModel.Route(routeInfoState.toRouteInfoPresentationModel())
            } else {
                RouteInfoStateRoutePresentationModel.Empty
            }
        }.flowOn(
            Dispatchers.Main
        ).stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
            RouteInfoStateRoutePresentationModel.Empty
        )
    }

    fun setRouteTransportMode(transportModeIndex: Int) {

        viewModelScope.launch {

            setRouteTransportModeUseCase(
                transportModeIndex = transportModeIndex
            )
        }
    }

    fun resetRoute() {

        viewModelScope.launch {


            resetRouteUseCase(all = false)
        }
    }

    fun removePlaceFromRoute(placeUUID: String) {

        viewModelScope.launch {

            removePlaceFromRouteUseCase(
                placeUUID = placeUUID
            )
        }
    }

    fun reorderRoute(newPosition: Int, placeUUID: String) {

        viewModelScope.launch {

            reorderRouteUseCase(
                newPosition = newPosition,
                placeUUID = placeUUID
            )
        }
    }

    fun optimizeRoute() {

        viewModelScope.launch {

            optimizeRouteUseCase()
        }
    }

    fun startNavigation() {

        viewModelScope.launch {

            initNavigationUseCase(
                transportMode = getRouteTransportModeUseCase()
            )

            navigateToPlaceInRouteUseCase()
        }
    }

    fun setSelectedPlace(uuid: String) {

        val fromInspected = findPlaceByUUIDInInspectUseCase(
            placeUUID = uuid
        )

        val fromSearch = findPlaceByUUIDInSearchUseCase(
            placeUUID = uuid
        )

        if (fromInspected != null) {
            setSelectedPlaceUseCase(
                selectedPlace = fromInspected.toPlaceRoutePresentationModel().toPlaceSelectedPlaceDomainModel()
            )
        } else {
            setSelectedPlaceUseCase(
                selectedPlace = fromSearch!!.toPlaceRoutePresentationModel().toPlaceSelectedPlaceDomainModel()
            )
        }
    }

    fun resetSelectedPlace() {

        viewModelScope.launch {

            resetSelectedPlaceUseCase()
        }
    }
}
package com.example.travel_mate.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.features.findcustom.domain.models.CustomPlaceMapDataCustomPlaceDomainModel
import com.example.features.navigation.domain.models.NavigationMapDataNavigationDomainModel
import com.example.features.inspect.domain.models.map.InspectTripMapDataInspectTripDomainModel
import com.example.features.inspect.domain.usecases.FindPlaceByUUIDInInspectUseCase
import com.example.features.findcustom.domain.usecases.GetCustomPlaceMapDataUseCase
import com.example.features.inspect.domain.usecases.GetInspectedTripMapDataUseCase
import com.example.features.navigation.domain.usecases.GetNavigationCurrentLocationUseCase
import com.example.features.navigation.domain.usecases.GetNavigationMapDataUseCase
import com.example.features.findcustom.domain.usecases.SetCustomPlaceUseCase
import com.example.features.route.domain.usecases.GetRouteMapDataUseCase
import com.example.features.route.domain.usecases.InitRouteWithSelectedStartUseCase
import com.example.features.savetrip.domain.usecases.InitSaveFromSearch
import com.example.features.search.domain.usecases.FindPlaceByUUIDInSearchUseCase
import com.example.features.search.domain.usecases.GetFullSearchStartUseCase
import com.example.features.search.domain.usecases.GetSearchOptionsUseCase
import com.example.features.search.domain.usecases.GetSearchPlacesDataUseCase
import com.example.features.search.domain.usecases.GetSearchStartDataUseCase
import com.example.features.search.presentation.mappers.toPlaceSaveTripDomainModel
import com.example.features.search.presentation.models.PlaceSearchPresentationModel
import com.example.features.selectedplace.domain.usecases.SetSelectedPlaceUseCase
import com.example.features.selectedplaceoptions.domain.usecases.GetMainSelectedPlaceOptionsUseCase
import com.example.features.selectedplaceoptions.domain.usecases.SetOriginOfSelectedPlaceUseCase
import com.example.features.selectedplaceoptions.domain.usecases.SetStateOfSelectedPlaceContainerUseCase
import com.example.travel_mate.ui.mappers.combine
import com.example.travel_mate.ui.mappers.toCoordinatesMapDataPresentationModel
import com.example.travel_mate.ui.mappers.toMapDataMapPresentationModel
import com.example.travel_mate.ui.mappers.toMapDataPresentationModel
import com.example.travel_mate.ui.mappers.toPlaceDataMapPresentationModel
import com.example.travel_mate.ui.mappers.toPlaceFullMapPresentationModel
import com.example.travel_mate.ui.mappers.toPlaceMapPresentationModel
import com.example.travel_mate.ui.mappers.toPlaceRouteDomainModel
import com.example.travel_mate.ui.mappers.toPlaceSelectedPlaceDomainModel
import com.example.travel_mate.ui.mappers.toSelectedPlaceOptionsPresentationModel
import com.example.travel_mate.ui.models.CoordinatesMapPresentationModel
import com.example.travel_mate.ui.models.MapDataMapPresentationModel
import com.example.travel_mate.ui.models.MapStateMapPresentationModel
import com.example.travel_mate.ui.models.PlaceDataMapPresentationModel
import com.example.travel_mate.ui.models.SelectedPlaceOptionsMainPresentationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelMain(
    private val getCustomPlaceMapDataUseCase: GetCustomPlaceMapDataUseCase,
    private val getInspectedTripMapDataUseCase: GetInspectedTripMapDataUseCase,
    private val getNavigationMapDataUseCase: GetNavigationMapDataUseCase,
    private val getRouteMapDataUseCase: GetRouteMapDataUseCase,
    private val getSearchStartDataUseCase: GetSearchStartDataUseCase,
    private val getSearchPlacesDataUseCase: GetSearchPlacesDataUseCase,
    private val getFullSearchStartUseCase: GetFullSearchStartUseCase,
    private val getSearchOptionsUseCase: GetSearchOptionsUseCase,
    private val getNavigationCurrentLocationUseCase: GetNavigationCurrentLocationUseCase,
    private val findPlaceByUUIDInSearchUseCase: FindPlaceByUUIDInSearchUseCase,
    private val findPlaceByUUIDInInspectUseCase: FindPlaceByUUIDInInspectUseCase,
    private val setSelectedPlaceUseCase: SetSelectedPlaceUseCase,
    private val getMainSelectedPlaceOptionsUseCase: GetMainSelectedPlaceOptionsUseCase,
    private val setOriginOfSelectedPlaceUseCase: SetOriginOfSelectedPlaceUseCase,
    private val setStateOfSelectedPlaceContainerUseCase: SetStateOfSelectedPlaceContainerUseCase,
    private val setCustomPlaceUseCase: SetCustomPlaceUseCase,
    private val initRouteWithSelectedStartUseCase: InitRouteWithSelectedStartUseCase,
    private val initSaveFromSearch: InitSaveFromSearch
): ViewModel() {

    val selectedPlaceOptions: StateFlow<SelectedPlaceOptionsMainPresentationModel> by lazy {

        getMainSelectedPlaceOptionsUseCase().map {

            it.toSelectedPlaceOptionsPresentationModel()
        }.flowOn(
            Dispatchers.Main
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedPlaceOptionsMainPresentationModel()
        )
    }

    private val _navigationLocationState: MutableStateFlow<CoordinatesMapPresentationModel?> = MutableStateFlow(
        null)
    val navigationLocationState: StateFlow<CoordinatesMapPresentationModel?> = _navigationLocationState.asStateFlow()

    private val _mapState: MutableStateFlow<MapStateMapPresentationModel> = MutableStateFlow(
        MapStateMapPresentationModel())
    val mapState: StateFlow<MapStateMapPresentationModel> = _mapState.asStateFlow()

    init {

        viewModelScope.launch {

            combine(
                getSearchStartDataUseCase(),
                getSearchPlacesDataUseCase(),
                getSearchOptionsUseCase(),
                getCustomPlaceMapDataUseCase(),
                getInspectedTripMapDataUseCase(),
                getRouteMapDataUseCase(),
                getNavigationMapDataUseCase(),
                _mapState
            ) { searchStart, searchPlaces, searchOptions, customPlace, inspectedTrip, route, navigation, main ->

                if (navigation is NavigationMapDataNavigationDomainModel.NavigationMapData) {

                    main.copy (
                        mapData = navigation.toMapDataMapPresentationModel()
                    )
                } else if (navigation is NavigationMapDataNavigationDomainModel.Arrived) {

                    main.copy (
                        mapData = MapDataMapPresentationModel.NavigationArrived
                    )
                } else if(
                    route.polylines.size > 1
                ) {
                    if (inspectedTrip is InspectTripMapDataInspectTripDomainModel.Inspected)

                        main.copy (
                            mapData = MapDataMapPresentationModel.Route(
                                startPlace = inspectedTrip.startMapData.toPlaceMapPresentationModel(),
                                polylines = route.polylines,
                                places = inspectedTrip.daysMapData.flatMap { day -> day.placesMapData.map { it.toPlaceMapPresentationModel() } }
                            )
                        )
                    else
                        main.copy(
                            mapData = MapDataMapPresentationModel.Route(
                                startPlace = searchStart!!.toPlaceDataMapPresentationModel(),
                                polylines = route.polylines,
                                places = searchPlaces.places.map { it.toPlaceDataMapPresentationModel()}
                            )
                        )
                } else if(

                    customPlace is CustomPlaceMapDataCustomPlaceDomainModel.CustomPlace
                ) {
                    main.copy(
                        mapData = MapDataMapPresentationModel.CustomPlace(
                            place = customPlace.toPlaceMapPresentationModel()
                        )
                    )
                } else if(inspectedTrip is InspectTripMapDataInspectTripDomainModel.Inspected) {

                    setOriginOfSelectedPlaceUseCase("inspect")

                    main.copy(
                        mapData = inspectedTrip.toMapDataPresentationModel()
                    )
                } else {

                    setOriginOfSelectedPlaceUseCase("search")

                    main.copy(
                        mapData = MapDataMapPresentationModel.Search(
                            startPlace = searchStart?.toPlaceDataMapPresentationModel(),
                            places = searchPlaces.places.map { it.toPlaceDataMapPresentationModel() },
                            parametersSelected = searchOptions.parametersSelected,
                            transportSelected = searchOptions.transportSelected
                        )
                    )
                }
            }.collect { newMain ->
                _mapState.value = newMain
            }
        }

        viewModelScope.launch {

            getNavigationCurrentLocationUseCase().map { location ->

                Log.d("currentLocationMap", "running")

                location.currentLocation.toCoordinatesMapDataPresentationModel()
            }.collectLatest {

                _navigationLocationState.value = it
            }
        }
    }


    fun setSelectedPlace(placeUUID: String) {

        viewModelScope.launch {

            val selectedPlaceDom =
                findPlaceByUUIDInSearchUseCase(placeUUID = placeUUID)?.toPlaceFullMapPresentationModel()?:
            findPlaceByUUIDInInspectUseCase(placeUUID = placeUUID)?.toPlaceFullMapPresentationModel()

            if (selectedPlaceDom != null) {

                setSelectedPlaceUseCase(
                    selectedPlaceDom.toPlaceSelectedPlaceDomainModel()
                )
            }
        }
    }

    fun setSelectedPlaceContainerState(state: String) {

        viewModelScope.launch {

            setStateOfSelectedPlaceContainerUseCase(
                containerState = state
            )
        }
    }

    fun setCustomPlace(geoPoint: GeoPoint) {

        viewModelScope.launch {

            setCustomPlaceUseCase(
                clickedPoint = geoPoint
            )
        }
    }

    fun initRouteWith(startPlace: PlaceDataMapPresentationModel?) {

        viewModelScope.launch {

            if(startPlace!=null)
                initRouteWithSelectedStartUseCase(
                    startPlace = startPlace.toPlaceRouteDomainModel()
                )
        }
    }

    fun initSaveWith() {

        viewModelScope.launch {

            val startPlace = getFullSearchStartUseCase()

            if (startPlace != null) {

                initSaveFromSearch(
                    startPlace = startPlace.toPlaceSaveTripDomainModel()
                )
            }
        }
    }
}
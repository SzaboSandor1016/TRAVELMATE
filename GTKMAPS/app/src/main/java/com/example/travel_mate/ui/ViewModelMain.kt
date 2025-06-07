package com.example.travel_mate.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travel_mate.data.Coordinates
import com.example.travel_mate.data.Place
import com.example.travel_mate.data.Route
import com.example.travel_mate.data.RouteNode
import com.example.travel_mate.data.RouteStep
import com.example.travel_mate.domain.SearchOptionsRepository
import com.example.travel_mate.data.TripRepositoryImpl
import com.example.travel_mate.domain.CurrentTripRepository
import com.example.travel_mate.domain.CustomPlaceRepository
import com.example.travel_mate.domain.GetCurrentLocationUseCase
import com.example.travel_mate.domain.GetLocationStartPlaceUseCase
import com.example.travel_mate.domain.InitRouteUseCase
import com.example.travel_mate.domain.NavigationRepository
import com.example.travel_mate.domain.RouteRepository
import com.example.travel_mate.domain.SearchRepository
import com.example.travel_mate.domain.InitSearchUseCase
import com.example.travel_mate.domain.NavigateToNextPlaceUseCase
import com.example.travel_mate.domain.SearchAutocompleteUseCase
import com.example.travel_mate.domain.SearchPlacesUseCase
import com.example.travel_mate.domain.SetCustomPlaceUseCase
import com.example.travel_mate.domain.SetSearchMinuteUseCase
import com.example.travel_mate.domain.SetSearchTransportModeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import java.util.Stack

//@HiltViewModel
class ViewModelMain /*@Inject*/ constructor(
    private val searchRepository: SearchRepository,
    private val searchOptionsRepository: SearchOptionsRepository,
    private val customPlaceRepository: CustomPlaceRepository,
    private val routeRepository: RouteRepository,
    private val navigationRepository: NavigationRepository,
    private val currentTripRepository: CurrentTripRepository,
    private val searchPlacesUseCase: SearchPlacesUseCase,
    private val setSearchTransportModeUseCase: SetSearchTransportModeUseCase,
    private val setSearchMinuteUseCase: SetSearchMinuteUseCase,
    private val navigateToNextPlaceUseCase: NavigateToNextPlaceUseCase,
    private val searchAutocompleteUseCase: SearchAutocompleteUseCase,
    private val initSearchUseCase: InitSearchUseCase,
    private val initRouteUseCase: InitRouteUseCase,
    private val setCustomPlaceUseCase: SetCustomPlaceUseCase,
    private val getLocationStartPlaceUseCase: GetLocationStartPlaceUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase
): ViewModel() {

    private val _mainContentState = MutableStateFlow(MainContentState())
    val mainContentState: StateFlow<MainContentState> = _mainContentState.asStateFlow()

    private val _mainSearchState = MutableStateFlow(MainSearchState())
    val mainSearchState: StateFlow<MainSearchState> = _mainSearchState.asStateFlow()

    private val _mainRouteState = MutableStateFlow(MainRouteState())
    val mainRouteState: StateFlow<MainRouteState> = _mainRouteState.asStateFlow()

    private val _mainNavigationState = MutableStateFlow(MainNavigationState())
    val mainNavigationState: StateFlow<MainNavigationState> = _mainNavigationState.asStateFlow()

    private val _mainNavigationInfoState = MutableStateFlow(MainNavigationInfoState())
    val mainNavigationInfoState: StateFlow<MainNavigationInfoState> = _mainNavigationInfoState.asStateFlow()

    private val _mainInspectTripState = MutableStateFlow(MainInspectTripState())
    val mainInspectTripState: StateFlow<MainInspectTripState> = _mainInspectTripState.asStateFlow()

    private val _mainStartPlaceState = MutableStateFlow(MainStartPlaceState())
    val mainStartPlaceState: StateFlow<MainStartPlaceState> = _mainStartPlaceState.asStateFlow()

    private val _chipsState = MutableStateFlow(MainChipsState())
    val chipsState: StateFlow<MainChipsState> = _chipsState.asStateFlow()

    private val _placeState = MutableStateFlow(CurrentPlaceState())
    val placeState: StateFlow<CurrentPlaceState> = _placeState.asStateFlow()

    private val _mainCustomPlaceState = MutableStateFlow(MainCustomPlaceState())
    val mainCustomPlaceState: StateFlow<MainCustomPlaceState> = _mainCustomPlaceState.asStateFlow()

    private val _mainErrorState = MutableStateFlow(ErrorGroup.NOTHING)
    val mainErrorState: StateFlow<ErrorGroup> = _mainErrorState.asStateFlow()

    init {

        viewModelScope.launch {

            combine(
                searchRepository.searchState,
                _mainSearchState
            ) { search, uiState ->

                uiState.copy(
                    places = search.search.getPlaces().map { place: Place ->
                        PlaceProcessed(
                            uuid = place.uUID,
                            coordinates = place.getCoordinates(),
                            title = place.getName().toString(),
                            category = place.getCategory().toString(),
                            containedByTrip = place.isContainedByTrip(),
                            containedByRoute = place.isContainedByRoute()
                        )
                    }
                )

            }.collect { newState ->
                _mainSearchState.value = newState
            }
        }

        viewModelScope.launch {

            combine(
                searchRepository.searchState,
                _mainStartPlaceState
            ) { search, uiState ->

                uiState.copy(
                    startPlace = search.search.getStartPlace()
                )

            }.collect { newState ->
                _mainStartPlaceState.value = newState
            }
        }

        viewModelScope.launch {

            combine(
                searchOptionsRepository.searchOptions,
                _chipsState
            ) {searchOptions, chipsState ->

                chipsState.copy(
                    transportMode = searchOptions.transportMode,
                    distance = searchOptions.distance
                )

            }.collect { newState ->

               _chipsState.value = newState
            }
        }

        viewModelScope.launch {

            combine(
                searchRepository.searchState,
                _placeState
            ) { searchState, placeState ->

                placeState.copy(
                    currentPlace = searchState.currentPlace,
                    containerState = placeState.containerState,
                    containerHeight = placeState.containerHeight
                )
            }.collect { newState ->
                _placeState.value = newState
            }

        }

        viewModelScope.launch {

            combine(
                routeRepository.routeState,
                _mainRouteState
            ) { routeState, mainRouteState ->

                if (routeState.route.getRouteNodes().size >= 2) {

                    setMainContentId(
                        contentId = MainContent.ROUTE
                    )
                }

                mainRouteState.copy(
                    route = routeState.route
                )

            }.collect { newState ->

                _mainRouteState.value = newState
            }

        }

        viewModelScope.launch {

            combine(
                navigationRepository.navigationState,
                _mainNavigationState
            ) { navigationState, mainNavigationState ->

                if (navigationState.navigationGoal != null) {

                    setMainContentId(
                        contentId = MainContent.NAVIGATION
                    )
                }

                mainNavigationState.copy(
                    navigationRouteNode = navigationState.navigationGoal,
                    navigationPolyline = navigationState.routePolyLines,
                    currentLocation = navigationState.currentLocation,
                )

            }.collect { newState ->

                _mainNavigationState.value = newState
            }

        }

        viewModelScope.launch {

            combine(
                _mainNavigationInfoState,
                navigationRepository.navigationInfoState
            ) { mainNavigationInfoState, routeNavigationInfoState ->

                mainNavigationInfoState.copy(
                    startedFrom = routeNavigationInfoState.startedFrom,
                    endOfRoute = routeNavigationInfoState.endOfRoute,
                    prevRouteStep = routeNavigationInfoState.prevRouteStep,
                    currentRouteStep = routeNavigationInfoState.currentRouteStep
                )
            }.collect { newState ->
                _mainNavigationInfoState.value = newState
            }
        }

        viewModelScope.launch {

            combine(
                currentTripRepository.currentTripState,
                _mainInspectTripState
            ) { currentTripState, mainInspectTripState ->

                Log.d("refreshInspect", "refreshInspect")

                var start: String? = null

                if (currentTripState.trip != null) {

                    setupNewTrip(
                        startPlace = currentTripState.trip.startPlace,
                        places = currentTripState.trip.places
                    )
                    start = currentTripState.trip.startPlace.getName().toString() +
                            " ," +
                            currentTripState.trip.startPlace.getAddress()?.getFullAddress()
                                .toString()

                    if (!mainInspectTripState.editing) {
                        setMainContentId(
                            contentId = MainContent.INSPECT
                        )
                    }
                } else {
                    returnToPrevContent()
                    resetDetails(
                        allDetails = true
                    )
                }

                mainInspectTripState.copy(
                    start = start,
                    inspectedTripIdentifier = currentTripState.tripIdentifier
                )

            }.collect { newState ->

                _mainInspectTripState.value = newState
            }
        }
        viewModelScope.launch {

            combine(
                customPlaceRepository.customPlace,
                _mainCustomPlaceState
            ) { customPlace, mainCustomPlace ->

                if (customPlace.customPlace != null) {

                    setMainContentId(
                        contentId = MainContent.CUSTOM
                    )
                }

                mainCustomPlace.copy(
                    customPlace = customPlace.customPlace
                )

            }.collect { newState ->

                _mainCustomPlaceState.value = newState
            }
        }
    }

    fun searchAutocomplete(
        query: String
    ) {

        viewModelScope.launch {

            try {

                searchAutocompleteUseCase(query).collect { places ->

                    _mainSearchState.update {

                        it.copy(
                            startPlaces = places
                        )
                    }
                }

            } catch (e: RuntimeException) {
                Log.e("Error during searching autocomplete... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.SEARCH_AUTO)
            } catch (e: IOException) {
                Log.e("Error during searching autocomplete... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.SEARCH_AUTO)
            } catch (e: Exception) {
                Log.e("Error during searching autocomplete... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.SEARCH_AUTO)
            }
        }
    }

    fun setErrorGroupType(errorType: ErrorGroup) {

        _mainErrorState.update {

            errorType
        }
    }

    fun setMainContentId(contentId: MainContent) {

        viewModelScope.launch {

            val currentContentId = _mainContentState.value.currentContentId

            if (currentContentId != contentId) {

                val prevContents = _mainContentState.value.prevContents

                val currentContent = _mainContentState.value.currentContentId

                prevContents.push(currentContent)


                _mainContentState.update {

                    it.copy(
                        prevContents = prevContents.clone() as Stack<MainContent>,
                        currentContentId = contentId
                    )
                }
            }
        }
    }

    fun returnToPrevContent() {

        viewModelScope.launch {

            val prevContents = _mainContentState.value.prevContents

            if (prevContents.isNotEmpty()) {

                val last = prevContents.pop()

                _mainContentState.update {

                    it.copy(
                        prevContents = prevContents.clone() as Stack<MainContent>,
                        currentContentId = last
                    )
                }
            }
        }
    }

    fun setContainerState(state: String){
        //this._containerState.postValue(state)

        _placeState.update {
            it.copy(containerState = state)
        }
    }

    fun setStartPlace(startPlace: Place){

        viewModelScope.launch {
            searchRepository.setSearchStartPlace(startPlace)
        }
    }

    fun setTransportMode(optionIndex: Int){

        viewModelScope.launch {

            setSearchTransportModeUseCase(
                index = optionIndex
            )
            routeRepository.setRouteTransportMode(
                index = optionIndex
            )
        }
    }

    fun setMinute(optionIndex: Int){

        viewModelScope.launch {

            setSearchMinuteUseCase(
                index = optionIndex
            )
        }
    }

    fun resetDetails(allDetails: Boolean){

        viewModelScope.launch {

            searchRepository.resetSearchDetails(all = allDetails)

            routeRepository.resetRoute(all = allDetails)

        }
    }

    fun initNewSearchAndRoute(startPlace: Place) {

        viewModelScope.launch {

            val places = emptyList<Place>();

            initSearchUseCase(
                startPlace = startPlace,
                places = places
            )

            initRouteUseCase(
                startPlace = startPlace
            )
        }
    }

    fun removePlacesByCategory(category: String){

        viewModelScope.launch {

            searchRepository.removePlacesByCategory(
                category = category
            )
        }
    }

    //todo note rewrite this to useCase too
    fun resetCurrentTripInRepository() {

        viewModelScope.launch {

            currentTripRepository.resetCurrentTrip()
        }
    }

    fun cancelEditInspected() {

        viewModelScope.launch {

            _mainInspectTripState.update {

                it.copy(
                    editing = false
                )
            }
        }
    }

    fun editInspectedTrip() {

        viewModelScope.launch {

            returnToPrevContent()

            _mainInspectTripState.update {

                it.copy(
                    editing = true
                )
            }
        }
    }

    fun setupNewTrip(startPlace: Place, places: List<Place>) {

        viewModelScope.launch {

            initSearchUseCase(
                startPlace = startPlace,
                places = places
            )
            initRouteUseCase(
                startPlace = startPlace
            )
        }
    }

    fun addRemovePlaceToTrip(uuid: String){

        viewModelScope.launch {

            searchRepository.addRemovePlaceToTrip(
                uuid = uuid
            )
        }
    }

    fun getStartPlace(): Place? {

        return searchRepository.getStartPlace()
    }

    fun getPlacesContainedByTrip(): List<Place> {

        return searchRepository.getPlacesContainedByTrip()
    }

    fun clearPlacesAddedToTrip() {

        viewModelScope.launch {

            searchRepository.clearPlacesAddedToTrip()
        }
    }

    fun getCurrentPlaceByUUID(uuid: String){

        viewModelScope.launch {

            searchRepository.getCurrentPlaceByUUID(
                uuid = uuid
            )
        }
    }

    fun resetCurrentPlace() {

        viewModelScope.launch {

            searchRepository.resetCurrentPlace()
        }
    }

    fun setFragmentContainerHeight(height :Int) {
        //_fragmentContainerHeight.value = height

        _placeState.update {
            it.copy(containerHeight = height)
        }
    }

    fun setExtendedSearchVisible(isExtended: Boolean) {
        _chipsState.update {
            it.copy(extendedSearchVisible = isExtended)
        }
    }

    fun setExtendedSearchSelected(isSelected: Boolean) {
        _chipsState.update {
            it.copy(extendedSearchSelected = isSelected)
        }
    }

    fun resetExtendedSearch() {
        _chipsState.update {
            it.copy(extendedSearchSelected = false, extendedSearchVisible = false)
        }
    }
    fun setCurrentChipGroup(id: Int, chipContent: List<FragmentSearch.ChipCategory>) {

        _chipsState.update {

            it.copy(currentChipGroup = id, currentChipGroupContent = chipContent.toList())
        }
    }

    fun setCurrentChipGroupContent( chipContent: List<FragmentSearch.ChipCategory>) {

        _chipsState.update {

            it.copy(currentChipGroupContent = chipContent.toList())
        }
    }

    fun resetCurrentChipGroup() {
        _chipsState.update {

            it.copy(currentChipGroup = null, currentChipGroupContent = null)
        }
    }

    fun addSelectedChip(
        index: Int
    ) {

        _chipsState.update { it ->
            val currentSelected = it.selectedChips.map { it }.plus(index)

            it.copy(selectedChips = currentSelected.toList())
        }
    }

    fun removeSelectedChip(
        index: Int
    ) {

        _chipsState.update {
            val currentSelected = it.selectedChips.map { it }.minus(index)

            it.copy(selectedChips = currentSelected.toList())
        }
    }

    fun resetStartPlaces() {
        _mainSearchState.update {
            it.copy(
                startPlaces = emptyList()
            )
        }
    }

    fun searchReverseGeoCodeStartPlace(){

        viewModelScope.launch {

            try {

                getLocationStartPlaceUseCase()

            } catch (e: RuntimeException) {
                Log.e("Error during searching reverse GeoCode... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.REVERSE_GEO_CODE)
            } catch (e: IOException) {
                Log.e("Error during searching reverse GeoCode... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.REVERSE_GEO_CODE)
            } catch (e: Exception) {
                Log.e("Error during searching reverse GeoCode... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.REVERSE_GEO_CODE)
            }
        }
    }

    fun searchOverpass(
        content: String,
        lat: String,
        lon: String,
        city: String,
        category: String
    ) {

        viewModelScope.launch {

            val coordinates = Coordinates(
                latitude = lat.toDouble(),
                longitude = lon.toDouble()
            )

            searchPlacesUseCase(
                content = content,
                coordinates = coordinates,
                city = city,
                category = category
            )
        }
    }

    fun setRouteTransportMode(index: Int) {

        viewModelScope.launch {

            routeRepository.setRouteTransportMode(
                index = index
            )
        }
    }

    fun setSelectedRouteNodePosition(coordinates: Coordinates?) {

        viewModelScope.launch {

            _mainRouteState.update {

                it.copy(
                    selectedRouteNodePosition = coordinates
                )
            }
        }
    }

    fun resetRoute() {

        viewModelScope.launch {

            searchRepository.resetRouteDetails()
            routeRepository.resetRoute(all = false)
        }
    }

    fun addRemovePlaceToRoute(uuid: String){

        viewModelScope.launch {

            val place = searchRepository.addRemovePlaceToRoute(
                uuid = uuid
            )

            routeRepository.addRemovePlaceToRoute(
                place = place
            )
        }
    }

    fun reorderRoute(newPosition: Int, nodeToMove: RouteNode) {

        viewModelScope.launch {

            try {

                routeRepository.reorderRoute(
                    newPosition = newPosition,
                    nodeToMove = nodeToMove
                )
            } catch (e: RuntimeException) {
                Log.e("Error during reordering route... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.OTHER)
            } catch (e: Exception) {
                Log.e("Error during reordering route... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.OTHER)
            }
        }

    }

    fun optimizeRoute() {

        viewModelScope.launch {

            try {

                routeRepository.optimizeRoute()
            } catch (e: RuntimeException) {
                Log.e("Error during optimizing route... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.OTHER)
            } catch (e: Exception) {
                Log.e("Error during optimizing route... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.OTHER)
            }
        }
    }

    fun navigateToNextPlaceInRoute() {

        viewModelScope.launch {
            try {

                navigateToNextPlaceUseCase()
            } catch (e: RuntimeException) {
                Log.e("Error during navigation... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.NAVIGATION)
            } catch (e: IOException) {
                Log.e("Error during navigation... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.NAVIGATION)
            } catch (e: Exception) {
                Log.e("Error during navigation... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.NAVIGATION)
            }
        }
    }

    fun startNavigationToCustomPlace(
        coordinates: Coordinates,
        transportMode: String
    ) {

        viewModelScope.launch {

            try {

                navigationRepository.navigateToCustomPlace(
                    coordinates = coordinates,
                    transportMode = transportMode
                )

            } catch (e: RuntimeException) {
                Log.e("Error during navigation to custom place... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.NAVIGATION)
            } catch (e: okio.IOException) {
                Log.e("Error during navigation to custom place... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.NAVIGATION)
            }
        }
    }

    fun stopNavigation() {

        navigationRepository.stopNavigationJobs(
            removeData = true
        )
    }

    fun getInitialCurrentLocation() {

        viewModelScope.launch {

            try {

                getCurrentLocationUseCase()

            } catch (e: RuntimeException) {
                Log.e("Error during getting initial current location... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.LOCATION)
            } catch (e: Exception) {
                Log.e("Error during getting initial current location... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.LOCATION)
            }
        }
    }

    fun getCustomPlace(position: GeoPoint) {

        viewModelScope.launch {

            try {

                setCustomPlaceUseCase(position)

            }catch (e: RuntimeException) {
                Log.e("Error during searching for custom place... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.CUSTOM_PLACE)
            } catch (e: IOException) {
                Log.e("Error during searching for custom place... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.CUSTOM_PLACE)
            } catch (e: Exception) {
                Log.e("Error during searching for custom place... Message: ", e.toString())
                setErrorGroupType(ErrorGroup.CUSTOM_PLACE)
            }
        }
    }

    fun resetCustomPlace() {

        viewModelScope.launch {

            customPlaceRepository.resetCustomPlace()
        }
    }

    data class MainContentState(
        val prevContents: Stack<MainContent> = Stack(),
        val currentContentId: MainContent = MainContent.SEARCH //0 -> search Fragment, 1 -> route fragment,
                                        // 2 -> inspect trip fragment, 3 -> custom place, 4 -> navigation
    )

    data class MainChipsState(
        val extendedSearchVisible: Boolean = false,
        val extendedSearchSelected: Boolean = false,
        val currentChipGroup: Int? = null,
        val currentChipGroupContent: List<FragmentSearch.ChipCategory>? = null,
        val selectedChips: List<Int> = emptyList(),
        val distance: Double = 0.0,
        val transportMode: String? = null,
    ) {
        val showExtendedSearch = extendedSearchVisible && !extendedSearchSelected
    }

    data class MainStartPlaceState(
        val startPlace: Place? = null
    )
    data class MainSearchState(
        val startPlaces: List<Place> = emptyList(),
        val currentPlaceUUID: String? = null,
        val places: List<PlaceProcessed> = emptyList(),
    ) {
        val isTripEmpty: Boolean = places.none { it.containedByTrip == true }
        val isRouteEmpty: Boolean = places.none { it.containedByRoute == true }
    }

    data class MainCustomPlaceState(
        val canAddCustomPlace: Boolean = true,
        val customPlace: Place? = null
    )

    data class MainInspectTripState(
        val editing: Boolean = false,
        val start: String? = null,
        val inspectedTripIdentifier: TripRepositoryImpl.TripIdentifier? = null
    )

    data class MainRouteState(
        val route: Route = Route(),
        val selectedRouteNodePosition: Coordinates? = null,
    )

    data class MainNavigationState(
        val navigationRouteNode: RouteNode? = null,
        val navigationPolyline: Polyline? = null,
        val currentLocation: Coordinates? = null
    )

    data class MainNavigationInfoState(
        val startedFrom: Int = 0,
        val endOfRoute: Boolean = false,
        val endOfNavigation: Boolean = false,
        val prevRouteStep: RouteStep? = null,
        val currentRouteStep: RouteStep? = null,
    ) {
        var showToNextDestination: Boolean = endOfRoute && !endOfNavigation
    }

    data class CurrentPlaceState(
        val currentPlace: Place? = null,
        val containerState: String = "collapsed",
        val containerHeight: Int = 0,
        )

    data class PlaceProcessed(val uuid: String,
                              val title: String,
                              val coordinates: Coordinates,
                              val category: String,
                              val containedByTrip: Boolean = false,
                              val containedByRoute: Boolean = false)

    enum class MainContent {
        SEARCH, INSPECT, ROUTE, NAVIGATION, CUSTOM
    }

    enum class ErrorGroup {
        NOTHING, OTHER, CUSTOM_PLACE,
        LOCATION, INIT_SEARCH, SEARCH_AUTO,
        REVERSE_GEO_CODE, SEARCH, NAVIGATION
    }
}
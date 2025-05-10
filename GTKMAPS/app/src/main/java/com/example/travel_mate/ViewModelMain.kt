package com.example.travel_mate

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import java.util.Stack

//@HiltViewModel
class ViewModelMain /*@Inject*/ constructor(
    private val searchRepository: SearchRepository,
    private val routeRepository: RouteRepository,
    private val tripRepository: TripRepository
): ViewModel() {

    private val _mainContentState = MutableStateFlow(MainContentState())
    val mainContentState: StateFlow<MainContentState> = _mainContentState.asStateFlow()

    private val _mainSearchState = MutableStateFlow(MainSearchState())
    val mainSearchState: StateFlow<MainSearchState> = _mainSearchState.asStateFlow()

    private val _mainRouteState = MutableStateFlow(MainRouteState())
    val mainRouteState: StateFlow<MainRouteState> = _mainRouteState.asStateFlow()

    private val _mainNavigationState = MutableStateFlow(MainNavigationState())
    val mainNavigationState: StateFlow<MainNavigationState> = _mainNavigationState.asStateFlow()

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
                            category = place.getCategory()!!,
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
                searchRepository.searchState,
                searchRepository.searchOptions,
                _chipsState
            ) { search, searchOptions, chipsState ->

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
            ) {  routeState, mainRouteState ->

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
                routeRepository.navigationState,
                _mainNavigationState
            ) {  navigationState, mainNavigationState ->

                if (navigationState.navigationGoal != null) {

                    setMainContentId(
                        contentId = MainContent.NAVIGATION
                    )
                }

                mainNavigationState.copy(
                    endOfRoute = navigationState.endOfRoute,
                    navigationRouteNode = navigationState.navigationGoal,
                    navigationPolyline = navigationState.routePolyLines,
                    prevRouteStep = navigationState.prevRouteStep,
                    currentLocation = navigationState.currentLocation,
                    currentRouteStep = navigationState.currentRouteStep
                )

            }.collect { newState ->

                _mainNavigationState.value = newState
            }

        }

        viewModelScope.launch {

            combine(
                tripRepository.currentTripState,
                _mainInspectTripState
            ) {  currentTripState, mainInspectTripState ->

                Log.d("refreshInspect", "refreshInspect")

                var start: String? = null

                if (currentTripState.trip != null) {

                    setupNewTrip(
                        startPlace = currentTripState.trip.startPlace,
                        places = currentTripState.trip.places
                    )
                    start = currentTripState.trip.startPlace.getName().toString() +
                            " ," +
                            currentTripState.trip.startPlace.getAddress()?.getFullAddress().toString()

                    if (!mainInspectTripState.editing) {
                        setMainContentId(
                            contentId = MainContent.INSPECT
                        )
                    }
                }  else {
                    returnToPrevContent()
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

            combine (
                searchRepository.customPlace,
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

            searchRepository.setTransportMode(
                index = optionIndex
            )
            routeRepository.setRouteTransportMode(
                index = optionIndex
            )
        }
    }

    fun setMinute(optionIndex: Int){

        viewModelScope.launch {

            searchRepository.setMinute(
               index = optionIndex
            )
        }
    }

    fun resetDetails(){

        viewModelScope.launch {

            searchRepository.resetSearchDetails()
            routeRepository.resetRouteDetails()
        }
    }
    fun resetFullDetails(){

        viewModelScope.launch {

            searchRepository.resetFullSearchDetails()

            routeRepository.resetRoute()
        }
    }

    fun initNewSearch(startPlace: Place) {

        viewModelScope.launch {

            Log.d("viewModelStartPlaceTest2", startPlace.getName().toString())

            searchRepository.initNewSearch(
                startPlace = startPlace
            )
            routeRepository.initNewRoute(
                startPlace = startPlace
            )

            resetStartPlaces()
        }
    }

    fun removePlacesByCategory(category: String){

        viewModelScope.launch {

            searchRepository.removePlacesByCategory(
                category = category
            )
        }
    }

    fun resetCurrentTripInRepository() {

        viewModelScope.launch {

            tripRepository.resetCurrentTrip()
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

            searchRepository.initNewSearchFromTrip(
                startPlace = startPlace,
                places = places
            )
            routeRepository.initNewRoute(
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
    /*fun isTripPlaceEmpty() {
        //_tripState.value = trip.isPlacesEmpty()

        viewModelScope.launch {

            _uiState.update {
                it.copy(isTripEmpty = searchRepository.searchHasPlaceAddedToTrip())
            }
        }
    }*/

    /*fun isRoutePlaceEmpty() {
        //_tripState.value = trip.isPlacesEmpty()

        viewModelScope.launch {

            _uiState.update {
                it.copy(isRouteEmpty = searchRepository.searchHasPlaceAddedToRoute())
            }
        }
    }*/

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

    fun searchAutocomplete(
        query: String
    ) {

        viewModelScope.launch {

            val startPlaces = searchRepository.getStartPlaces(
                query = query
            )

            _mainSearchState.update {
                it.copy(
                    startPlaces = startPlaces
                )
            }
        }
    }

    fun searchReverseGeoCode(){

        viewModelScope.launch {

            val place = searchRepository.getReverseGeoCode()

            if (place != null)
                routeRepository.initNewRoute(
                    startPlace = place
                )
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

            searchRepository.fetchPlaces(
                content = content,
                lat = lat,
                lon = lon,
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
            routeRepository.resetRouteDetails()
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

            routeRepository.reorderRoute(
                newPosition = newPosition,
                nodeToMove = nodeToMove
            )
        }

    }

    fun optimizeRoute() {

        viewModelScope.launch {

            routeRepository.optimizeRoute()
        }
    }

    fun startNavigationThroughPlacesInRoute() {

        viewModelScope.launch {

            routeRepository.navigateToPlaceInRoute()
        }
    }

    fun navigateToNextPlaceInRoute() {

        viewModelScope.launch {

            routeRepository.navigateToNextPlaceInRoute()
        }
    }

    fun startNavigationToCustomPlace(
        coordinates: Coordinates,
        transportMode: String
    ) {

        viewModelScope.launch {

            routeRepository.navigateToCustomPlace(
                coordinates = coordinates,
                transportMode = transportMode
            )
        }
    }

    fun stopNavigation() {

        routeRepository.stopNavigationJob()

        routeRepository.stopExtrapolationLoop()
    }

    fun getInitialCurrentLocation() {

        viewModelScope.launch {

            routeRepository.getCurrentLocation()
        }
    }

    fun getCustomPlace(position: GeoPoint) {

        viewModelScope.launch {

            searchRepository.getCustomPlace(
                clickedPoint = position
            )
        }
    }

    fun resetCustomPlace() {

        viewModelScope.launch {

            searchRepository.resetCustomPlace()
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
        val inspectedTripIdentifier: TripRepository.TripIdentifier? = null
    )

    data class MainRouteState(
        val route: Route = Route(),
        val selectedRouteNodePosition: Coordinates? = null,
    )

    data class MainNavigationState(
        val endOfRoute: Boolean = false,
        val navigationRouteNode: RouteNode? = null,
        val navigationPolyline: Polyline? = null,
        val currentLocation: Coordinates? = null,
        val prevRouteStep: RouteStep? = null,
        val currentRouteStep: RouteStep? = null
    )

    data class PlaceProcessed(val uuid: String,
                              val title: String,
                              val coordinates: Coordinates,
                              val category: String,
                              val containedByTrip: Boolean = false,
                              val containedByRoute: Boolean = false)

    data class CurrentPlaceState(
        val currentPlace: Place? = null,
        val containerState: String = "collapsed",
        val containerHeight: Int = 0,
        )

    enum class MainContent {
        SEARCH, INSPECT, ROUTE, NAVIGATION, CUSTOM
    }
}
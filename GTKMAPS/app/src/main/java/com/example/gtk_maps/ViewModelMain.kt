package com.example.gtk_maps

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ViewModelMain: ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()


    /*private val _containerState: MutableLiveData<String> = MutableLiveData<String>()
    val containerState: LiveData<String> = _containerState

    private val _fragmentContainerHeight = MutableLiveData<Double?>()
    val fragmentContainerHeight: LiveData<Double?> = _fragmentContainerHeight

    private val _tripState: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val tripState: LiveData<Boolean> = _tripState

    private val _currentPlace = MutableLiveData<ClassPlace?>()
    val currentPlace: LiveData<ClassPlace?> = _currentPlace

    private val _currentPlaceState = MutableLiveData<Boolean>()
    val currentPlaceState: LiveData<Boolean> = _currentPlaceState

    private val _startPlace = MutableLiveData<ClassPlace?>()
    val startPlace: LiveData<ClassPlace?> = _startPlace

    private val _places = MutableLiveData<ArrayList<ClassPlace>>()
    val places: LiveData<ArrayList<ClassPlace>> = _places

    private val _transportMode = MutableLiveData<String?>()
    val transportMode: LiveData<String?> = _transportMode

    private val _minute = MutableLiveData<Int?>()
    val minute: LiveData<Int?> = _minute*/

    private val search = ClassSearch()

    private val trip = ClassTrip()

    fun setContainerState(state: String){
        //this._containerState.postValue(state)

        _uiState.update {
            it.copy(containerState = state)
        }
    }

    fun getCurrentSearch(): ClassSearch = search

    fun getCurrentTrip(): ClassTrip = trip

    fun setStartPlace(startPlace: ClassPlace){

        search.setStartPlace(startPlace).also {
            trip.setStartPlace(startPlace)
            //_startPlace.postValue(startPlace)
        }

        _uiState.update {
            it.copy(startPlace = startPlace)
        }
    }

    fun getStartPlace(): ClassPlace?{

        return search.getStartPlace()
    }

/*    fun setPlaces(places: ArrayList<ClassPlace>){

        search.setPlaces(places).also{

            _places.postValue(places)
        }
    }*/

    fun getPlaces(): ArrayList<ClassPlace>{

        return search.getPlaces()
    }

/*    fun addPlace(place: ClassPlace){

        search.addPlace(place)
        _places.postValue(search.getPlaces())
    }*/

    fun addPlaces(places: ArrayList<ClassPlace>){

        val newList = search.addPlaces(places)

        _uiState.update {
            Log.d("countOrig",it.places.size.toString())

            it.copy(places = newList.toList())
        }

        Log.d("countUpdated", _uiState.value.places.size.toString())


    }
    //_places.postValue(search.getPlaces())
    /*_places.value = search.getPlaces()*/

    /*Log.d("test", _places.value!![0].getName()!!)*/

    /*fun removePlace(place: ClassPlace){

        search.removePlace(place)
        _places.postValue(search.getPlaces())
    }

    fun removePlaces(places: ArrayList<ClassPlace>){

        search.removePlaces(places)
        _places.postValue(search.getPlaces())
    }*/

    fun setTransportMode(optionIndex: Int){

        val mode = when (optionIndex) {
            0 -> "walk" // walk
            1 -> "car" // car
            else -> null

        }

        search.setTransportMode(mode)

        _uiState.update {
            it.copy(transportMode = mode)
        }

        //_transportMode.postValue(mode)
    }

    fun getTransportMode(): String?{
        return search.getTransportMode()
    }

    fun setMinute(optionIndex: Int){

        val minute = when (optionIndex) {
            0 -> 15
            1 -> 30
            2 -> 45
            else -> null

        }

        search.setMinute(minute)

        search.calculateDistance()
        Log.d("distance" , search.getDistance().toString())

        _uiState.update {
            it.copy(extendedSearchSelected = true)
        }

        /*_uiState.update {
            it.copy(minute = minute)
        }*/
        //_minute.postValue(minute)
    }

    fun getMinute(): Int?{

        return search.getMinute()
    }

    fun calculateDistance(){

        search.calculateDistance()
    }

    fun getCalculatedDistance(): Double?{

        return search.getDistance()
    }

    fun resetDetails(){

        search.resetSearchDetails(). also {

            trip.clear()

            _uiState.update {
                val newList: ArrayList<ClassPlace> = ArrayList()
                it.copy(/*minute = null, transportMode = null,*/ places = newList)
            }

            /*_minute.postValue(null)
            _transportMode.postValue(null)
            _places.postValue(ArrayList())*/
        }
    }

    fun removePlacesByCategory(category: String){

        _uiState.update {
            Log.d("countOrig",it.places.size.toString())

            val newList = search.removePlacesByCategory(category)
            if (newList.isNotEmpty()) {
                it.copy(places = newList.toList() as ArrayList)
            } else {
                it.copy(places = ArrayList())
            }
        }
        Log.d("countRemoved",_uiState.value.places.size.toString())
        //_places.postValue(search.getPlaces())
    }

    fun setupNewTrip(trip: ClassTrip) {

        this.search.setStartPlace(trip.getStartPlace()!!)
        this.search.setPlaces(trip.getPlaces())
        this.trip.setPlaces(trip.getPlaces())

        /*_startPlace.postValue(trip.getStartPlace())
        _places.postValue(trip.getPlaces())*/

        this.trip.setUUIDFromOtherTrip(trip.getUUID()!!)
        this.trip.setNote(trip.getNote())
        this.trip.setDate(trip.getDate())
        this.trip.setTitle(trip.getTitle())

        _uiState.update {
            it.copy(startPlace = trip.getStartPlace()!!, places = trip.getPlaces())
        }

    }

    fun setUUID(){
        trip.setUUID()
    }

    fun getUUID(): String? {
        return trip.getUUID()
    }

    /*fun setTripStartPlace(startPlace: ClassPlace){
        trip.setStartPlace(startPlace)
    }
    fun getTripStartPlace(): ClassPlace?{
        return trip.getStartPlace()
    }
    fun setTripPlaces(places: ArrayList<ClassPlace>){

        trip.setPlaces(places)
    }*/
    fun getTripPlaces(): ArrayList<ClassPlace>{
        return trip.getPlaces()
    }
    fun addPlaceToTrip(place: ClassPlace){

        trip.addPlace(place)

    }
    fun removePlaceFromTrip(place: ClassPlace){

        trip.removePlace(place)
    }
    fun setTripDate(date: String){
        trip.setDate(date)
    }
    fun getTripDate(): String{
        return trip.getDate()
    }
    fun setTripTitle(title: String){
        trip.setTitle(title)
    }
    fun getTripTitle(): String{
        return trip.getTitle()
    }
    fun setTripNote(note: String){
        trip.setNote(note)
    }
    fun getTripNote(): String{
        return trip.getNote()
    }
    fun addTripContributors(contributors: ArrayList<String>){
        trip.addContributors(contributors)
    }
    fun addTripContributor(contributor: String){
        trip.addContributor(contributor)
    }
    fun removeTripContributors(contributors: ArrayList<String>){
        trip.removeContributors(contributors)
    }
    fun removeTripContributor(contributor: String){
        trip.removeContributor(contributor)
    }

    fun isTripPlaceEmpty(){
        //_tripState.value = trip.isPlacesEmpty()
        _uiState.update {
            it.copy(tripEmpty = trip.isPlacesEmpty())
        }
    }
    /*fun isTripPlacesEmpty(): Boolean{
        return trip.isPlacesEmpty()
    }*/

    fun getTripPlaceCount(): Int{
        return trip.getSize()
    }
    fun clearTrip() {
        trip.clear()
    }

    fun setCurrentPlace(place: ClassPlace) {
        //_currentPlace.value = place

        _uiState.update {
            it.copy(currentPlace = place)
        }
    }

    fun setCurrentPlaceState(isAdded: Boolean) {
        //_currentPlaceState.value = isAdded

        _uiState.update {
            it.copy(currentPlaceState = isAdded)
        }
    }

    fun setFragmentContainerHeight(height :Int) {
        //_fragmentContainerHeight.value = height

        _uiState.update {
            it.copy(containerHeight = height)
        }
    }

    fun setExtendedSearch(isExtended: Boolean) {
        _uiState.update {
            it.copy(extendedSearchVisible = isExtended)
        }
    }

    fun resetExtendedSearch() {
        _uiState.update {
            it.copy(extendedSearchSelected = false, extendedSearchVisible = false)
        }
    }
    fun setCurrentChipGroup(id: Int, chipContent: List<ActivityMainUIController.ChipCategory>) {

        _uiState.update {

            it.copy(currentChipGroup = id, currentChipGroupContent = chipContent.toList())
        }
    }
    fun resetCurrentChipGroup() {
        _uiState.update {

            it.copy(currentChipGroup = null, currentChipGroupContent = null)
        }
    }

    fun addSelectedChip(index: Int) {

        _uiState.update {
            val currentSelected = it.selectedChips.map { it }.plus(index)

            it.copy(selectedChips = currentSelected.toList())
        }
    }
    fun removeSelectedChip(index: Int) {

        _uiState.update {
            val currentSelected = it.selectedChips.map { it }.minus(index)

            it.copy(selectedChips = currentSelected.toList())
        }
    }


    /*fun searchAutocomplete(query: String) {

        viewModelScope.launch {

            val startPlaces = startPlacesRepository.searchAutocomplete(query)

            _startPlaces.postValue(startPlaces)
        }
    }

    fun searchReverseGeoCode(coordinates: ClassCoordinates){

        viewModelScope.launch {

            val reverseGeoCode = startPlacesRepository.searchReverseGeoCode(coordinates)

            _startPlaces.postValue(reverseGeoCode)
        }
    }

    fun searchOverpass(query: String, category: String) {

        viewModelScope.launch {

            val places = placesRepository.searchOverpass(query,category)

            addPlaces(places)
        }
    }*/

    data class MainUiState(
        val containerState: String = "collapsed",
        val containerHeight: Int = 0,
        val currentPlace: ClassPlace = ClassPlace(),
        val currentPlaceState: Boolean = false,
        val startPlace: ClassPlace? = null,
        val places: List<ClassPlace> = ArrayList(),
        val extendedSearchVisible: Boolean = false,
        val extendedSearchSelected: Boolean = false,
        val transportMode: String? = null,
        /*val minute: Int? = null,*/
        val tripEmpty: Boolean = true,
        val currentChipGroup: Int? = null,
        val currentChipGroupContent: List<ActivityMainUIController.ChipCategory>? = null,
        val selectedChips: List<Int> = emptyList(),
        ) {
        var showExtendedSearch = extendedSearchVisible && !extendedSearchSelected
    }

    /*private val _startPlace = MutableLiveData<ClassPlace?>()
    val startPlace: LiveData<ClassPlace?> = _startPlace

    private val _places = MutableLiveData<ArrayList<ClassPlace>>()
    val places: LiveData<ArrayList<ClassPlace>> = _places

    private val _transportMode = MutableLiveData<String?>()
    val transportMode: LiveData<String?> = _transportMode

    private val _minute = MutableLiveData<Int?>()
    val minute: LiveData<Int?> = _minute*/
}
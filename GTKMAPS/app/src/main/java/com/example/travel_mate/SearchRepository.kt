package com.example.travel_mate

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class SearchRepository /*@Inject*/ constructor(
    private val overpassRemoteDataSource: OverpassRemoteDataSource,
    private val photonRemoteDataSource: PhotonRemoteDataSource,
    private val locationLocalDataSource: LocationLocalDataSource
    ) {

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _searchOptions = MutableStateFlow(SearchOptions())
    val searchOptions: StateFlow<SearchOptions> = _searchOptions.asStateFlow()

    suspend fun setSearchStartPlace(startPlace: Place) {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(
                    search = it.search.setStartPlace(
                        startPlace = startPlace
                    )
                )
            }
        }
    }

    suspend fun setTransportMode(index: Int) {

        CoroutineScope(coroutineContext).launch {

            setSearchTransportMode(
                index = index
            )

        }
    }

    suspend fun setSearchTransportMode(index: Int) {
        withContext(Dispatchers.IO) {

            val mode = when (index) {
                0 -> "walk" // walk
                1 -> "car" // car
                else -> null

            }

            _searchOptions.update {

                it.copy(
                    transportMode = mode
                )
            }
        }
    }

    suspend fun setMinute(index: Int) {

        withContext(Dispatchers.IO) {

            val minute = when (index) {
                0 -> 15
                1 -> 30
                2 -> 45
                else -> 0

            }
            _searchOptions.update {

                it.copy(
                    minute = minute
                )
            }
        }
    }

    suspend fun  resetSearchDetails() {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(

                    search = it.search.setStartPlace(
                        startPlace = it.search.getStartPlace()
                    )
                )
            }

        }
    }



    suspend fun  resetFullSearchDetails() {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(
                    search = Search()
                )
            }

        }
    }

    suspend fun  resetRouteDetails() {

        withContext(Dispatchers.IO) {

            clearPlacesAddedToRoute()
        }
    }
    suspend fun initNewSearch(startPlace: Place) {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(
                    search = Search().setStartPlace(
                        startPlace = startPlace
                    )
                )
            }
        }
    }

    suspend fun initNewSearchFromTrip(startPlace: Place, places: List<Place>) {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(
                    search = Search(
                        startPlace = startPlace,
                        places = places
                    )
                )
            }
        }

    }

    suspend fun getCurrentPlaceByUUID(uuid: String){

        withContext(Dispatchers.IO) {

            val currentPlace = _searchState.value.search.getPlaceByUUID(uuid)!!

            _searchState.update {

                it.copy(
                    currentPlace = currentPlace
                )
            }
        }
    }
    fun getStartPlace(): Place? {
        return _searchState.value.search.getStartPlace()
    }

    fun getPlacesContainedByTrip(): List<Place> {

        return _searchState.value.search.getPlacesContainedByTrip()
    }

    suspend fun addRemovePlaceToTrip(uuid: String){

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(
                    search = it.search.selectPlaceByUUIDForTrip(
                        uuid = uuid
                    ),
                    currentPlace = it.currentPlace.containedByTrip(
                        contained = !it.currentPlace.isContainedByTrip()
                    )
                )
            }
        }
    }

    suspend fun addRemovePlaceToRoute(uuid: String): Place{

        return withContext(Dispatchers.IO) {

            val place = _searchState.value.search.getPlaceByUUID(
                uuid = uuid)!!


            if (place.uUID == _searchState.value.currentPlace.uUID)
                _searchState.update {

                    it.copy(
                        currentPlace = it.currentPlace.containedByRoute(
                            contained = !it.currentPlace.isContainedByRoute()
                        )
                    )
                }

            _searchState.update {

                it.copy(
                    search = it.search.selectPlaceByUUIDForRoute(
                        uuid = uuid
                    )
                )
            }
            return@withContext place
        }
    }

    suspend fun clearPlacesAddedToTrip() {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(
                    search = it.search.clearPlacesAddedToTrip()
                )
            }
        }

    }
    suspend fun clearPlacesAddedToRoute() {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(
                    search = it.search.copy().clearPlacesAddedToRoute()
                )
            }
        }

    }

    suspend fun removePlacesByCategory(category: String) {

        withContext(Dispatchers.IO) {

            _searchState.update {

                it.copy(

                    search = it.search.setPlaces(it.search.getPlaces().filter { it.getCategory() != category })
                )
            }
        }
    }


    suspend fun fetchPlaces(content: String, lat: String, lon: String, city: String, category: String) {

        withContext(Dispatchers.IO) {

            val places = when (_searchOptions.value.distance) {
                0.0 -> {
                    overpassRemoteDataSource.fetchPlacesByCity(
                        content = content,
                        city = city,
                        category = category
                    )

                }
                else -> {
                    overpassRemoteDataSource.fetchPlacesByCoordinates(
                        content = content,
                        lat = lat,
                        lon = lon,
                        dist = _searchOptions.value.distance,
                        category = category
                    )
                }
            }

            _searchState.update {

                it.copy(

                    search = it.search.addPlaces(places)
                )
            }
        }
    }


    suspend fun getStartPlaces(query: String): List<Place> {

        return photonRemoteDataSource.getStartPlaces(query)
    }

    suspend fun getReverseGeoCode(): Place? {

        return withContext(Dispatchers.IO) {

            try {

                val coordinates = getCurrentLocation()

                if (coordinates != null) {

                    val reverseGeoCode = async {
                        photonRemoteDataSource.getReverseGeoCode(
                            coordinates = coordinates
                        )
                    }

                    val reverseGeoResult = reverseGeoCode.await()

                    if (reverseGeoResult.isNotEmpty() == true) {
                        initNewSearch(reverseGeoResult[0])

                        return@withContext reverseGeoResult[0]
                    } else {
                        return@withContext null
                    }
                } else {
                    return@withContext null
                }

            } catch (e: Error) {
                Log.e("FindUserLocation", "get user location: error")
            }
            return@withContext null
        }
    }

    /** [getCurrentLocation]
     *  ask for a location update, generate a [Coordinates] from it and return
     */
    private suspend fun getCurrentLocation(): Coordinates?{

        return withContext(Dispatchers.IO) {

            val location = locationLocalDataSource.getCurrentLocation()

            Log.d("locationRequest", location?.latitude.toString() + " " + location?.longitude.toString())

            if (location != null) {
                return@withContext Coordinates(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            }
            return@withContext null
        }
    }

    data class SearchState(val search: Search = Search(),
                           val currentPlace: Place = Place()
    )

    data class SearchOptions(var transportMode: String? = null,
                             var minute: Int = 0
    ) {

        private var speed = when (transportMode) {
            "walk" -> 3500 // walk
            "car" -> 50000 // car
            else -> 0
        }

        var distance = (this.speed.times(this.minute)) / 60.0
    }

}
package com.example.travel_mate.data

import com.example.travel_mate.domain.SearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class SearchRepositoryImpl: SearchRepository  {

    private val overpassRemoteDataSource: OverpassRemoteDataSource by inject(OverpassRemoteDataSource::class.java)
    private val photonRemoteDataSource: PhotonRemoteDataSource by inject(PhotonRemoteDataSource::class.java)
    private val routeRemoteDataSource: RouteRemoteDataSource by inject(RouteRemoteDataSource::class.java)

    private val searchCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val _searchState = MutableStateFlow(SearchState())

    override val searchState: StateFlow<SearchState> = _searchState.asStateFlow()
    override suspend fun testInitNewSearch(
        startPlace: Place,
        places: List<Place>
    ): Search {
        return withContext(searchCoroutineDispatcher) {

            _searchState.update {

                val search = Search(
                    startPlace = startPlace,
                    places = places
                )

                it.copy(
                    search = search
                )
            }

            return@withContext _searchState.value.search
        }
    }

    override suspend fun testRemovePlacesByCategory(category: String): Search {

        return withContext(searchCoroutineDispatcher) {

            val newPlacesList = _searchState.value.search.getPlaces().filter { it.getCategory() != category }

            val newSearch = _searchState.value.search.setPlaces(
                newPlacesList
            )

            _searchState.update {

                it.copy(

                    search = newSearch
                )
            }

            return@withContext _searchState.value.search
        }
    }

    override suspend fun testResetSearchDetails(all: Boolean): Search {

        return withContext(searchCoroutineDispatcher) {

            when(all) {

                true -> _searchState.update {

                    it.copy(
                        search = Search()
                    )
                }

                false -> _searchState.update {

                    it.copy(

                        search = it.search.setStartPlace(
                            startPlace = it.search.getStartPlace()
                        )
                    )
                }
            }
            return@withContext _searchState.value.search
        }
    }


    override suspend fun getSearchStartPlace(): Place? {
        return _searchState.value.search.getStartPlace();
    }

    override suspend fun getSearchPlaces(): List<Place> {
        return _searchState.value.search.getPlaces();
    }

    override suspend fun searchAutocomplete(query: String): Flow<PhotonResponse> {
        return photonRemoteDataSource.getStartPlaces(
            query = query
        )
    }

    override suspend fun testFetchPlacesByDistance(
        distance: Double,
        content: String,
        coordinates: Coordinates,
        category: String
    ) :Search {
        return withContext(searchCoroutineDispatcher) {

            val places = overpassRemoteDataSource.fetchPlacesByCoordinates(
                content = content,
                lat = coordinates.getLatitude().toString(),
                lon = coordinates.getLongitude().toString(),
                dist = distance,
                category = category
            )

            _searchState.update {

                it.copy(

                    search = it.search.addPlaces(places)
                )
            }
            return@withContext _searchState.value.search
        }
    }

    override suspend fun testFetchPlacesByCity(
        content: String,
        city: String,
        category: String
    ): Search {
        return withContext(searchCoroutineDispatcher) {

            val places = overpassRemoteDataSource.fetchPlacesByCity(
                content = content,
                city = city,
                category = category
            )

            _searchState.update {

                it.copy(

                    search = it.search.addPlaces(places)
                )
            }
            return@withContext _searchState.value.search
        }
    }

    override suspend fun setSearchStartPlace(startPlace: Place) {

        withContext(searchCoroutineDispatcher) {

            _searchState.update {

                it.copy(
                    search = it.search.setStartPlace(
                        startPlace = startPlace
                    )
                )
            }
        }
    }

    override suspend fun  resetSearchDetails( all: Boolean) {

        withContext(searchCoroutineDispatcher) {

            when(all) {

                true -> _searchState.update {

                    it.copy(
                        search = Search()
                    )
                }

                false -> _searchState.update {

                    it.copy(

                        search = it.search.setStartPlace(
                            startPlace = it.search.getStartPlace()
                        )
                    )
                }
            }
        }
    }

    override suspend fun  resetRouteDetails() {

        withContext(searchCoroutineDispatcher) {

            clearPlacesAddedToRoute()
        }
    }
    override suspend fun initNewSearch(startPlace: Place, places: List<Place>) {

        withContext(searchCoroutineDispatcher) {

            _searchState.update {

                val search = Search(
                    startPlace = startPlace,
                    places = places
                )

                it.copy(
                    search = search
                )
            }
        }
    }

    override suspend fun resetCurrentPlace(){

        withContext(searchCoroutineDispatcher) {

            _searchState.update {

                it.copy(
                    currentPlace = null
                )
            }
        }
    }

    override suspend fun getCurrentPlaceByUUID(uuid: String){

        withContext(searchCoroutineDispatcher) {

            val currentPlace = _searchState.value.search.getPlaceByUUID(uuid)!!

            _searchState.update {

                it.copy(
                    currentPlace = currentPlace
                )
            }
        }
    }
    override fun getStartPlace(): Place? {
        return _searchState.value.search.getStartPlace()
    }

    override fun getPlacesContainedByTrip(): List<Place> {

        return _searchState.value.search.getPlacesContainedByTrip()
    }

    override suspend fun addRemovePlaceToTrip(uuid: String){

        withContext(searchCoroutineDispatcher) {

            _searchState.update {

                it.copy(
                    search = it.search.selectPlaceByUUIDForTrip(
                        uuid = uuid
                    ),
                    currentPlace = it.currentPlace?.containedByTrip(
                        contained = !it.currentPlace.isContainedByTrip()
                    )
                )
            }
        }
    }

    override suspend fun addRemovePlaceToRoute(uuid: String): Place {

        return withContext(searchCoroutineDispatcher) {

            val place = _searchState.value.search.getPlaceByUUID(
                uuid = uuid
            )!!


            if (place.uUID == _searchState.value.currentPlace?.uUID)
                _searchState.update {

                    it.copy(
                        currentPlace = it.currentPlace?.containedByRoute(
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

    override suspend fun clearPlacesAddedToTrip() {

        withContext(searchCoroutineDispatcher) {

            _searchState.update {

                it.copy(
                    search = it.search.clearPlacesAddedToTrip()
                )
            }
        }

    }
    override suspend fun clearPlacesAddedToRoute() {

        withContext(searchCoroutineDispatcher) {

            _searchState.update {

                it.copy(
                    search = it.search.copy().clearPlacesAddedToRoute()
                )
            }
        }

    }

    override suspend fun removePlacesByCategory(category: String) {

        withContext(searchCoroutineDispatcher) {

            _searchState.update {

                it.copy(

                    search = it.search.setPlaces(
                        it.search.getPlaces().filter { it.getCategory() != category })
                )
            }
        }
    }


    override suspend fun fetchPlacesByCity(
        content: String,
        city: String,
        category: String
    ){

        return withContext(searchCoroutineDispatcher) {

            val places = overpassRemoteDataSource.fetchPlacesByCity(
                content = content,
                city = city,
                category = category
            )

            _searchState.update {

                it.copy(

                    search = it.search.addPlaces(places)
                )
            }
        }
    }

    override suspend fun fetchPlacesByDistance(
        distance: Double,
        content: String,
        coordinates: Coordinates,
        category: String
    ){

        return withContext(searchCoroutineDispatcher) {

            val places = overpassRemoteDataSource.fetchPlacesByCoordinates(
                content = content,
                lat = coordinates.getLatitude().toString(),
                lon = coordinates.getLongitude().toString(),
                dist = distance,
                category = category
            )

            _searchState.update {

                it.copy(

                    search = it.search.addPlaces(places)
                )
            }
        }
    }

    override suspend fun getReverseGeoCode(coordinates: Coordinates): Flow<ReverseGeoCodeResponse> {

            return routeRemoteDataSource.getReverseGeoCode(
                coordinates = coordinates
            )
    }

    data class SearchState(
        val search: Search = Search(),
        val currentPlace: Place? = null
    )

}
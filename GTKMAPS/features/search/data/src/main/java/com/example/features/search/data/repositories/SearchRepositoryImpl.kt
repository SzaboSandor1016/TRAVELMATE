package com.example.features.search.data.repositories

import com.example.core.remotedatasources.searchplacesdatasource.domain.OverpassRemoteDataSource
import com.example.core.remotedatasources.searchstartdatasource.domain.PhotonRemoteDataSource
import com.example.features.search.domain.mappers.mapToSearchPlace
import com.example.features.search.domain.mappers.toFlowOfSearchDataDomainModel
import com.example.features.search.domain.mappers.toFlowOfSearchInfoDomainModel
import com.example.features.search.domain.mappers.toFlowOfSearchPlaceDataDomainModel
import com.example.features.search.domain.models.searchmodels.PlaceSearchDomainModel
import com.example.features.search.domain.models.searchmodels.SearchDataSearchDomainModel
import com.example.features.search.domain.models.searchmodels.PlaceDataSearchDomainModel
import com.example.features.search.domain.models.searchmodels.SearchStartInfoSearchDomainModel
import com.example.features.search.domain.models.searchmodels.SearchSearchDomainModel
import com.example.features.search.domain.models.searchmodels.SearchStateSearchDomainModel
import com.example.features.search.domain.repositories.SearchRepository
import com.example.remotedatasources.responses.PhotonResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class SearchRepositoryImpl: SearchRepository  {

    private val overpassRemoteDataSource: OverpassRemoteDataSource by inject(OverpassRemoteDataSource::class.java)
    private val photonRemoteDataSource: PhotonRemoteDataSource by inject(PhotonRemoteDataSource::class.java)

    private val searchCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val _searchState = MutableStateFlow(SearchStateSearchDomainModel())

    override fun getSearchStartInfo(): Flow<SearchStartInfoSearchDomainModel> {

        return _searchState.toFlowOfSearchInfoDomainModel()
    }

    override fun getSearchStartData(): Flow<PlaceDataSearchDomainModel?> {
        return _searchState.toFlowOfSearchPlaceDataDomainModel()
    }

    override fun getPlaceByUUID(placeUUID: String): PlaceSearchDomainModel? {

        return _searchState.value.search.getPlaceByUUID(placeUUID)
    }

    override fun getSearchPlacesData(): Flow<SearchDataSearchDomainModel> {
        return _searchState.toFlowOfSearchDataDomainModel()
    }

    override suspend fun searchAutocomplete(query: String): Flow<PhotonResponse> {

        return withContext(searchCoroutineDispatcher) {

            photonRemoteDataSource.getStartPlaces(
                query = query
            )
        }
    }

    override suspend fun  resetSearchDetails( all: Boolean) {

        withContext(searchCoroutineDispatcher) {

            when(all) {

                true -> _searchState.update {

                    it.copy(
                        search = SearchSearchDomainModel()
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

    override suspend fun initNewSearch(startPlace: PlaceSearchDomainModel) {

        withContext(searchCoroutineDispatcher) {

            _searchState.update {

                val search = SearchSearchDomainModel(
                    startPlace = startPlace,
                    places = emptyList()
                )

                it.copy(
                    search = search
                )
            }
        }
    }

    override suspend fun getCurrentPlaceByUUID(uuid: String): PlaceSearchDomainModel?{

        return withContext(searchCoroutineDispatcher) {

            return@withContext _searchState.value.search.getPlaceByUUID(uuid)
        }
    }
    override fun getStartPlace(): PlaceSearchDomainModel? {
        return _searchState.value.search.getStartPlace()
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

            overpassRemoteDataSource.fetchPlacesByCity(
                content = content,
                city = city,
                category = category
            ).collect { response ->

                _searchState.update {

                    it.copy(

                        search = it.search.addPlaces(response.mapToSearchPlace(category))
                    )
                }
            }
        }
    }

    override suspend fun fetchPlacesByDistance(
        distance: Double,
        content: String,
        centerLat: Double,
        centerLon: Double,
        category: String
    ){

        return withContext(searchCoroutineDispatcher) {

            overpassRemoteDataSource.fetchPlacesByCoordinates(
                content = content,
                lat = centerLat.toString(),
                lon = centerLon.toString(),
                dist = distance,
                category = category
            ).collect { response ->

                _searchState.update {

                    it.copy(

                        search = it.search.addPlaces(response.mapToSearchPlace(category))
                    )
                }
            }
        }
    }
}
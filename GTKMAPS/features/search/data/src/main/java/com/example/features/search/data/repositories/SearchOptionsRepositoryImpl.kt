package com.example.features.search.data.repositories

import com.example.features.search.domain.mappers.toFlowOfSearchOptionsInfoDomainModel
import com.example.features.search.domain.models.searchoptionsmodels.SearchOptionsInfoSearchDomainModel
import com.example.features.search.domain.models.searchoptionsmodels.SearchOptionsStateSearchDomainModel
import com.example.features.search.domain.repositories.SearchOptionsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class SearchOptionsRepositoryImpl: SearchOptionsRepository {

    private val searchOptionsCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val _searchOptions = MutableStateFlow(SearchOptionsStateSearchDomainModel())
    override val searchOptions: StateFlow<SearchOptionsStateSearchDomainModel> = _searchOptions.asStateFlow()

    override fun getSearchOptionsInfo(): Flow<SearchOptionsInfoSearchDomainModel> {
        return _searchOptions.toFlowOfSearchOptionsInfoDomainModel()
    }

    override suspend fun resetSearchOptions() {

        withContext(searchOptionsCoroutineDispatcher) {

            _searchOptions.update {

                SearchOptionsStateSearchDomainModel()
            }
        }
    }


    override suspend fun testSetSearchTransportMode(index: Int): SearchOptionsStateSearchDomainModel {

        return withContext(searchOptionsCoroutineDispatcher) {

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
            return@withContext _searchOptions.value
        }
    }

    override suspend fun testSetMinute(index: Int): SearchOptionsStateSearchDomainModel {

        return withContext(searchOptionsCoroutineDispatcher) {

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
            return@withContext _searchOptions.value
        }
    }

    override suspend fun setSearchTransportMode(index: Int) {
        withContext(searchOptionsCoroutineDispatcher) {

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

    override suspend fun setMinute(index: Int) {

        withContext(searchOptionsCoroutineDispatcher) {

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

    override fun getMinute(): Int {
        return _searchOptions.value.minute
    }

    override fun getTransportMode(): String {
        return _searchOptions.value.transportMode.toString()
    }

    override fun getDistance(): Double {
        return _searchOptions.value.distance
    }
}
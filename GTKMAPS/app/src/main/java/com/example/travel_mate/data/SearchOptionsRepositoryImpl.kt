package com.example.travel_mate.data

import com.example.travel_mate.domain.SearchOptionsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class SearchOptionsRepositoryImpl: SearchOptionsRepository {

    private val searchOptionsCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val _searchOptions = MutableStateFlow(SearchOptions())
    override val searchOptions: StateFlow<SearchOptions> = _searchOptions.asStateFlow()
    override suspend fun testSetSearchTransportMode(index: Int): SearchOptions {

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

    override suspend fun testSetMinute(index: Int): SearchOptions {

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


    data class SearchOptions(
        var transportMode: String? = null,
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
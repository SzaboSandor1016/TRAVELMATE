package com.example.features.search.domain.repositories

import com.example.features.search.domain.models.searchoptionsmodels.SearchOptionsInfoSearchDomainModel
import com.example.features.search.domain.models.searchoptionsmodels.SearchOptionsStateSearchDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SearchOptionsRepository {

    val searchOptions: StateFlow<SearchOptionsStateSearchDomainModel>

    fun getSearchOptionsInfo(): Flow<SearchOptionsInfoSearchDomainModel>

    suspend fun resetSearchOptions()

    suspend fun testSetSearchTransportMode(index: Int): SearchOptionsStateSearchDomainModel

    suspend fun testSetMinute(index: Int): SearchOptionsStateSearchDomainModel

    suspend fun setSearchTransportMode(index: Int)

    suspend fun setMinute(index: Int)

    fun getMinute(): Int

    fun getTransportMode(): String

    fun getDistance(): Double
}
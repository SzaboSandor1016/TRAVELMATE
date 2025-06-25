package com.example.travel_mate.domain

import com.example.travel_mate.data.SearchOptionsRepositoryImpl.SearchOptions
import kotlinx.coroutines.flow.StateFlow

interface SearchOptionsRepository {

    val searchOptions: StateFlow<SearchOptions>

    suspend fun testSetSearchTransportMode(index: Int): SearchOptions

    suspend fun testSetMinute(index: Int): SearchOptions

    suspend fun setSearchTransportMode(index: Int)

    suspend fun setMinute(index: Int)

    fun getMinute(): Int

    fun getTransportMode(): String

    fun getDistance(): Double
}
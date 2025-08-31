package com.example.features.navigation.domain.repositories

import com.example.features.navigation.domain.models.CoordinatesNavigationDomainModel
import com.example.features.navigation.domain.models.CurrentLocationNavigationDomainModel
import com.example.features.navigation.domain.models.NavigationMapDataNavigationDomainModel
import com.example.features.navigation.domain.models.NavigationInfoNavigationDomainModel
import kotlinx.coroutines.flow.Flow

interface NavigationRepository {

    fun getNavigationInfo(): Flow<NavigationInfoNavigationDomainModel>

    fun getNavigationCurrentLocation(): Flow<CurrentLocationNavigationDomainModel>

    fun getNavigationMapData(): Flow<NavigationMapDataNavigationDomainModel>

    fun getCurrentNodeIndex(): Int

    fun initNavigation(navigationMode: String, destinationCoordinates: List<CoordinatesNavigationDomainModel>)

    fun resetNavigation()

    fun navigateToPlaceInRoute()

    fun stopNavigationJobs()

    suspend fun updateCurrentLocation(): CoordinatesNavigationDomainModel?
}
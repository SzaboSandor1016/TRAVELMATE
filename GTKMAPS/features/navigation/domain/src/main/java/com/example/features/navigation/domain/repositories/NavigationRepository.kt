package com.example.features.navigation.domain.repositories

import com.example.features.navigation.domain.models.CoordinatesNavigationDomainModel
import com.example.features.navigation.domain.models.CurrentLocationNavigationDomainModel
import com.example.features.navigation.domain.models.NavigationMapDataNavigationDomainModel
import com.example.features.navigation.domain.models.NavigationInfoNavigationDomainModel
import kotlinx.coroutines.flow.Flow

interface NavigationRepository {

    //val navigationState: StateFlow<NavigationDataStateModel>

    //val navigationInfoState: StateFlow<NavigationInfoStateModel>

    fun getNavigationInfo(): Flow<NavigationInfoNavigationDomainModel>

    fun getNavigationCurrentLocation(): Flow<CurrentLocationNavigationDomainModel>

    fun getNavigationMapData(): Flow<NavigationMapDataNavigationDomainModel>

    fun getCurrentNodeIndex(): Int

    //fun setEndOfNavigation(isEnd: Boolean)

    fun initNavigation(navigationMode: String, destinationCoordinates: List<CoordinatesNavigationDomainModel>)

    fun resetNavigation()

    fun navigateToPlaceInRoute()

    //fun navigateToNextPlaceInRoute(nextRouteNodeCoordinates: CoordinatesNavigationDomainModel)

    //fun navigateToCustomPlace(latitude: Double, longitude: Double, transportMode: String)

    //fun stopNavigationJob(/*removeData: Boolean*/)

    fun stopNavigationJobs(/*removeData: Boolean*/)

    //fun stopExtrapolationLoop()

    //fun restartNavigation(goalCoordinates: CoordinatesNavigationDomainModel/*, removeData: Boolean*/)

    suspend fun updateCurrentLocation(): CoordinatesNavigationDomainModel?
}
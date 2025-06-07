package com.example.travel_mate.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.travel_mate.data.RouteRepositoryImpl
import com.example.travel_mate.domain.SearchOptionsRepository
import com.example.travel_mate.data.SearchRepositoryImpl
import com.example.travel_mate.domain.CheckUserUseCase
import com.example.travel_mate.domain.CurrentTripRepository
import com.example.travel_mate.domain.CustomPlaceRepository
import com.example.travel_mate.domain.DeleteTripUseCase
import com.example.travel_mate.domain.DeleteUserUseCase
import com.example.travel_mate.domain.GetCurrentLocationUseCase
import com.example.travel_mate.domain.GetLocalTripsUseCase
import com.example.travel_mate.domain.GetLocationStartPlaceUseCase
import com.example.travel_mate.domain.GetNewContributorDataUseCase
import com.example.travel_mate.domain.GetRemoteContributedTripsUseCase
import com.example.travel_mate.domain.GetRemoteTripsUseCase
import com.example.travel_mate.domain.GetSelectableContributorsUseCase
import com.example.travel_mate.domain.GetSelectedTripDataUseCase
import com.example.travel_mate.domain.InitRouteUseCase
import com.example.travel_mate.domain.InitSearchUseCase
import com.example.travel_mate.domain.NavigateToNextPlaceUseCase
import com.example.travel_mate.domain.NavigationRepository
import com.example.travel_mate.domain.SaveTripUseCase
import com.example.travel_mate.domain.SaveTripWithUpdatedPlacesUseCase
import com.example.travel_mate.domain.SearchAutocompleteUseCase
import com.example.travel_mate.domain.SearchPlacesUseCase
import com.example.travel_mate.domain.SearchRepository
import com.example.travel_mate.domain.SetCurrentTripContributorsUseCase
import com.example.travel_mate.domain.SetCustomPlaceUseCase
import com.example.travel_mate.domain.SetSearchMinuteUseCase
import com.example.travel_mate.domain.SetSearchTransportModeUseCase
import com.example.travel_mate.domain.SetUserPermissionUseCase
import com.example.travel_mate.domain.SignInUserUseCase
import com.example.travel_mate.domain.SignOutUserUseCase
import com.example.travel_mate.domain.SignUpUserUseCase
import com.example.travel_mate.domain.TripRepository
import com.example.travel_mate.domain.UserRepository

class ViewModelFactory(
    private val tripRepository: TripRepository,
    private val searchRepository: SearchRepository,
    private val searchOptionsRepository: SearchOptionsRepository,
    private val customPlaceRepository: CustomPlaceRepository,
    private val routeRepository: RouteRepositoryImpl,
    private val navigationRepository: NavigationRepository,
    private val currentTripRepository: CurrentTripRepository,
    private val userRepository: UserRepository,
    private val searchPlacesUseCase: SearchPlacesUseCase,
    private val setSearchMinuteUseCase: SetSearchMinuteUseCase,
    private val setSearchTransportModeUseCase: SetSearchTransportModeUseCase,
    private val navigateToNextPlaceUseCase: NavigateToNextPlaceUseCase,
    private val searchAutocompleteUseCase: SearchAutocompleteUseCase,
    private val initSearchUseCase: InitSearchUseCase,
    private val initRouteUseCase: InitRouteUseCase,
    private val setCustomPlaceUseCase: SetCustomPlaceUseCase,
    private val getLocationStartPlaceUseCase: GetLocationStartPlaceUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val signUpUserUseCase: SignUpUserUseCase,
    private val signInUserUseCase: SignInUserUseCase,
    private val signOutUserUseCase: SignOutUserUseCase,
    private val checkUserUseCase: CheckUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val setUserPermissionUseCase: SetUserPermissionUseCase,
    private val getNewContributorDataUseCase: GetNewContributorDataUseCase,
    private val saveTripUseCase: SaveTripUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
    private val getLocalTripsUseCase: GetLocalTripsUseCase,
    private val getRemoteTripsUseCase: GetRemoteTripsUseCase,
    private val getRemoteContributedTripsUseCase: GetRemoteContributedTripsUseCase,
    private val getSelectedTripDataUseCase: GetSelectedTripDataUseCase,
    private val getSelectableContributorsUseCase: GetSelectableContributorsUseCase,
    private val saveTripWithUpdatedPlacesUseCase: SaveTripWithUpdatedPlacesUseCase,
    private val setCurrentTripContributorsUseCase: SetCurrentTripContributorsUseCase
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ViewModelUser::class.java) -> {
                ViewModelUser(
                    tripRepository,
                    currentTripRepository = currentTripRepository,
                    userRepository = userRepository,
                    signUpUserUseCase = signUpUserUseCase,
                    signInUserUseCase = signInUserUseCase,
                    signOutUserUseCase = signOutUserUseCase,
                    checkUserUseCase = checkUserUseCase,
                    deleteUserUseCase = deleteUserUseCase,
                    setUserPermissionUseCase = setUserPermissionUseCase,
                    getNewContributorDataUseCase = getNewContributorDataUseCase,
                    saveTripUseCase = saveTripUseCase,
                    deleteTripUseCase = deleteTripUseCase,
                    getLocalTripsUseCase = getLocalTripsUseCase,
                    getRemoteTripsUseCase = getRemoteTripsUseCase,
                    getRemoteContributedTripsUseCase = getRemoteContributedTripsUseCase,
                    getSelectedTripDataUseCase = getSelectedTripDataUseCase,
                    getSelectableContributorsUseCase = getSelectableContributorsUseCase,
                    saveTripWithUpdatedPlacesUseCase = saveTripWithUpdatedPlacesUseCase,
                    setCurrentTripContributorsUseCase = setCurrentTripContributorsUseCase
                ) as T
            }
            modelClass.isAssignableFrom(ViewModelMain::class.java) -> {
                ViewModelMain(
                    searchRepository = searchRepository,
                    searchOptionsRepository = searchOptionsRepository,
                    customPlaceRepository = customPlaceRepository,
                    routeRepository = routeRepository,
                    navigationRepository = navigationRepository,
                    currentTripRepository = currentTripRepository,
                    searchPlacesUseCase = searchPlacesUseCase,
                    setSearchMinuteUseCase = setSearchMinuteUseCase,
                    setSearchTransportModeUseCase = setSearchTransportModeUseCase,
                    navigateToNextPlaceUseCase = navigateToNextPlaceUseCase,
                    searchAutocompleteUseCase = searchAutocompleteUseCase,
                    initSearchUseCase = initSearchUseCase,
                    initRouteUseCase = initRouteUseCase,
                    setCustomPlaceUseCase = setCustomPlaceUseCase,
                    getLocationStartPlaceUseCase = getLocationStartPlaceUseCase,
                    getCurrentLocationUseCase = getCurrentLocationUseCase,
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown fragment type")
        }
    }

}
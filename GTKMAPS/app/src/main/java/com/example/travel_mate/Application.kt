package com.example.travel_mate

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.travel_mate.data.AppDatabase
import com.example.travel_mate.data.CurrentTripRepositoryImpl
import com.example.travel_mate.data.CustomPlaceRepositoryImpl
import com.example.travel_mate.data.FirebaseAuthenticationSourceImpl
import com.example.travel_mate.data.FirebaseRemoteDataSourceImpl
import com.example.travel_mate.data.LocationLocalDataSource
import com.example.travel_mate.data.LocationRepositoryImpl
import com.example.travel_mate.data.NavigationRepositoryImpl
import com.example.travel_mate.data.OverpassRemoteDataSourceImpl
import com.example.travel_mate.data.PhotonRemoteDataSourceImpl
import com.example.travel_mate.data.RoomLocalDataSourceImpl
import com.example.travel_mate.data.RouteNodeRepositoryImpl
import com.example.travel_mate.data.RouteRemoteDataSourceImpl
import com.example.travel_mate.data.RouteRepositoryImpl
import com.example.travel_mate.data.SearchOptionsRepositoryImpl
import com.example.travel_mate.data.SearchRepositoryImpl
import com.example.travel_mate.data.TripRepositoryImpl
import com.example.travel_mate.data.UserRepositoryImpl
import com.example.travel_mate.domain.CheckUserUseCase
import com.example.travel_mate.domain.DeleteLocalTripUseCase
import com.example.travel_mate.domain.DeleteRemoteTripUseCase
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
import com.example.travel_mate.domain.GetUsersByUIDsUseCase
import com.example.travel_mate.domain.InitRouteUseCase
import com.example.travel_mate.domain.InitSearchUseCase
import com.example.travel_mate.domain.NavigateToNextPlaceUseCase
import com.example.travel_mate.domain.ProcessTripIdentifiersUseCase
import com.example.travel_mate.domain.SaveLocalTripUseCase
import com.example.travel_mate.domain.SaveRemoteTripUseCase
import com.example.travel_mate.domain.SaveTripUseCase
import com.example.travel_mate.domain.SaveTripWithUpdatedPlacesUseCase
import com.example.travel_mate.domain.SearchAutocompleteUseCase
import com.example.travel_mate.domain.SearchPlacesUseCase
import com.example.travel_mate.domain.SearchReverseGeoCodeUseCase
import com.example.travel_mate.domain.SetCurrentTripContributorsUseCase
import com.example.travel_mate.domain.SetCustomPlaceUseCase
import com.example.travel_mate.domain.SetSearchMinuteUseCase
import com.example.travel_mate.domain.SetSearchTransportModeUseCase
import com.example.travel_mate.domain.SetUserPermissionUseCase
import com.example.travel_mate.domain.SignInUserUseCase
import com.example.travel_mate.domain.SignOutUserUseCase
import com.example.travel_mate.domain.SignUpUserUseCase
import com.example.travel_mate.domain.UpdateUserUseCase
import com.example.travel_mate.domain.UserRepository
import com.example.travel_mate.ui.ViewModelFactory

/** [Application]
 *  custom [Application]
 *  that creates a [ViewModelFactory] for the app's [ViewModel]s
 *  and builds the [Room] [AppDatabase]
 */
class Application: Application() {

    companion object {
        lateinit var appContext: Context
            private set
        lateinit var factory: ViewModelFactory

        lateinit var appDatabase: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext


        appDatabase = Room.databaseBuilder(
            context = appContext,
            klass = AppDatabase::class.java,
            name = "trip_local_database"
        ).build()

        val photonRemoteDataSource = PhotonRemoteDataSourceImpl()

        val locationLocalDataSource = LocationLocalDataSource(
            appContext
        )

        val locationRepository = LocationRepositoryImpl(
            locationLocalDataSource = locationLocalDataSource
        )

        val routeRemoteDataSource = RouteRemoteDataSourceImpl()

        val routeNodeRepository = RouteNodeRepositoryImpl(
            routeRemoteDataSource = routeRemoteDataSource
        )

        val routeRepository = RouteRepositoryImpl(
            routeNodeRepository = routeNodeRepository
        )

        val overpassRemoteDataSource = OverpassRemoteDataSourceImpl()

        val roomLocalDataSource = RoomLocalDataSourceImpl(
            appDatabase = appDatabase
        )

        val firebaseRemoteDataSource = FirebaseRemoteDataSourceImpl()

        val firebaseAuthenticationSource = FirebaseAuthenticationSourceImpl()

        val searchRepository = SearchRepositoryImpl(
            overpassRemoteDataSource = overpassRemoteDataSource,
            photonRemoteDataSource = photonRemoteDataSource,
            locationRepository = locationRepository
        )

        val tripRepository = TripRepositoryImpl(
            roomLocalDataSource = roomLocalDataSource,
            firebaseRemoteDataSource = firebaseRemoteDataSource,
        )
        
        val currentTripRepository = CurrentTripRepositoryImpl()
        
        val userRepository = UserRepositoryImpl(
            firebaseAuthenticationSource = firebaseAuthenticationSource
        )

        val navigationRepository = NavigationRepositoryImpl(
            routeNodeRepository = routeNodeRepository,
            locationRepository = locationRepository
        )

        val customPlaceRepository = CustomPlaceRepositoryImpl(
            photonRemoteDataSource = photonRemoteDataSource
        )

        val searchOptionsRepository = SearchOptionsRepositoryImpl()

        val searchPlacesUseCase = SearchPlacesUseCase(
            searchRepository = searchRepository,
            searchOptionsRepository = searchOptionsRepository
        )

        val setSearchTransportModeUseCase = SetSearchTransportModeUseCase(
            searchOptionsRepository = searchOptionsRepository
        )

        val setSearchMinuteUseCase = SetSearchMinuteUseCase(
            searchOptionsRepository = searchOptionsRepository
        )

        val navigateToNextPlaceUseCase = NavigateToNextPlaceUseCase(
            routeRepository = routeRepository,
            navigationRepository = navigationRepository
        )

        val searchAutocompleteUseCase = SearchAutocompleteUseCase(
            searchRepository = searchRepository
        )

        val initSearchUseCase = InitSearchUseCase(
            searchRepository = searchRepository
        )

        val initRouteUseCase = InitRouteUseCase(
            routeRepository = routeRepository
        )

        val searchReverseGeoCodeUseCase = SearchReverseGeoCodeUseCase(
            searchRepository = searchRepository
        )

        val setCustomPlaceUseCase = SetCustomPlaceUseCase(
            searchReverseGeoCodeUseCase = searchReverseGeoCodeUseCase,
            customPlaceRepository = customPlaceRepository
        )

        val getCurrentLocationUseCase = GetCurrentLocationUseCase(
            locationRepository = locationRepository
        )

        val getLocationStartPlaceUseCase = GetLocationStartPlaceUseCase(
            getCurrentLocationUseCase = getCurrentLocationUseCase,
            searchReverseGeoCodeUseCase = searchReverseGeoCodeUseCase,
            initSearchUseCase = initSearchUseCase
        )
        
        val updateUserUseCase = UpdateUserUseCase(
            userRepository = userRepository
        )
        
        val signUpUserUseCase = SignUpUserUseCase(
            userRepository = userRepository,
            updateUserUseCase = updateUserUseCase
        )
        
        val signInUserUseCase = SignInUserUseCase(
            userRepository = userRepository,
            updateUserUseCase = updateUserUseCase
        )
        
        val signOutUserUseCase = SignOutUserUseCase(
            userRepository = userRepository,
            updateUserUseCase = updateUserUseCase
        )
        
        val checkUserUseCase = CheckUserUseCase(
            userRepository = userRepository,
            updateUserUseCase = updateUserUseCase
        )
        
        val deleteUserUseCase = DeleteUserUseCase(
            userRepository = userRepository,
            tripRepository = tripRepository
        )
        
        val setUserPermissionUseCase = SetUserPermissionUseCase(
            userRepository = userRepository
        )
        
        val getNewContributorDataUseCase = GetNewContributorDataUseCase(
            userRepository = userRepository
        )

        val deleteLocalTripUseCase = DeleteLocalTripUseCase(
            tripRepository = tripRepository
        )

        val saveRemoteTripUseCase = SaveRemoteTripUseCase(
            tripRepository = tripRepository,
            userRepository = userRepository,
            deleteLocalTripUseCase = deleteLocalTripUseCase
        )

        val deleteRemoteTripUseCase = DeleteRemoteTripUseCase(
            tripRepository = tripRepository,
            userRepository = userRepository
        )
        
        val saveLocalTripUseCase = SaveLocalTripUseCase(
            tripRepository = tripRepository,
            deleteRemoteTripUseCase = deleteRemoteTripUseCase
        )

        val saveTripUseCase = SaveTripUseCase(
            saveLocalTripUseCase = saveLocalTripUseCase,
            saveRemoteTripUseCase = saveRemoteTripUseCase
        )
        
        val deleteTripUseCase = DeleteTripUseCase(
            deleteLocalTripUseCase = deleteLocalTripUseCase,
            deleteRemoteTripUseCase = deleteRemoteTripUseCase
        )

        val getLocalTripsUseCase = GetLocalTripsUseCase(
            tripRepository = tripRepository
        )


        val getUsersByUIDsUseCase = GetUsersByUIDsUseCase(
            userRepository = userRepository
        )

        val processTripIdentifiersUseCase = ProcessTripIdentifiersUseCase(
            userRepository = userRepository,
            getUsersByUIDsUseCase = getUsersByUIDsUseCase
        )
        
        val getRemoteTripsUseCase = GetRemoteTripsUseCase(
            tripRepository = tripRepository,
            userRepository = userRepository,
            processTripIdentifiersUseCase = processTripIdentifiersUseCase
        )
        
        val getRemoteContributedTripsUseCase = GetRemoteContributedTripsUseCase(
            tripRepository = tripRepository,
            userRepository = userRepository,
            processTripIdentifiersUseCase = processTripIdentifiersUseCase
        )
        
        val getSelectedTripDataUseCase = GetSelectedTripDataUseCase(
            tripRepository = tripRepository,
            currentTripRepository = currentTripRepository
        )
        
        val getSelectableContributorsUseCase = GetSelectableContributorsUseCase(
            userRepository = userRepository,
            currentTripRepository = currentTripRepository,
            getUsersByUIDsUseCase = getUsersByUIDsUseCase
        )
        val saveTripWithUpdatedPlacesUseCase = SaveTripWithUpdatedPlacesUseCase(
            currentTripRepository = currentTripRepository,
            saveTripUseCase = saveTripUseCase
        )
        
        val setCurrentTripContributorsUseCase = SetCurrentTripContributorsUseCase(
            currentTripRepository = currentTripRepository,
            userRepository = userRepository
        )
        
        factory = ViewModelFactory(
            tripRepository = tripRepository,
            searchRepository = searchRepository,
            customPlaceRepository = customPlaceRepository,
            routeRepository = routeRepository,
            navigationRepository = navigationRepository,
            currentTripRepository = currentTripRepository,
            searchOptionsRepository = searchOptionsRepository,
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
        )
    }

}
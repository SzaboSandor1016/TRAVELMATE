package com.example.travel_mate

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.travel_mate.data.AppDatabase
import com.example.travel_mate.data.CurrentTripRepositoryImpl
import com.example.travel_mate.data.CustomPlaceRepositoryImpl
import com.example.travel_mate.data.FirebaseAuthenticationSource
import com.example.travel_mate.data.FirebaseAuthenticationSourceImpl
import com.example.travel_mate.data.FirebaseRemoteDataSource
import com.example.travel_mate.data.FirebaseRemoteDataSourceImpl
import com.example.travel_mate.data.LocationLocalDataSource
import com.example.travel_mate.data.LocationRepositoryImpl
import com.example.travel_mate.data.NavigationRepositoryImpl
import com.example.travel_mate.data.OverpassRemoteDataSource
import com.example.travel_mate.data.OverpassRemoteDataSourceImpl
import com.example.travel_mate.data.PhotonRemoteDataSource
import com.example.travel_mate.data.PhotonRemoteDataSourceImpl
import com.example.travel_mate.data.RoomLocalDataSource
import com.example.travel_mate.data.RoomLocalDataSourceImpl
import com.example.travel_mate.data.RouteNodeRepositoryImpl
import com.example.travel_mate.data.RouteRemoteDataSource
import com.example.travel_mate.data.RouteRemoteDataSourceImpl
import com.example.travel_mate.data.RouteRepositoryImpl
import com.example.travel_mate.data.SearchOptionsRepositoryImpl
import com.example.travel_mate.data.SearchRepositoryImpl
import com.example.travel_mate.data.TripRepositoryImpl
import com.example.travel_mate.data.UserRepositoryImpl
import com.example.travel_mate.domain.CheckUserUseCase
import com.example.travel_mate.domain.CurrentTripRepository
import com.example.travel_mate.domain.CustomPlaceRepository
import com.example.travel_mate.domain.DeleteLocalTripUseCase
import com.example.travel_mate.domain.DeleteRemoteTripUseCase
import com.example.travel_mate.domain.DeleteTripUseCase
import com.example.travel_mate.domain.DeleteUserUseCase
import com.example.travel_mate.domain.GetCurrentLocationUseCase
import com.example.travel_mate.domain.GetLocalTripsUseCase
import com.example.travel_mate.domain.InitSearchAndRouteWithLocationStartUseCase
import com.example.travel_mate.domain.GetNewContributorDataUseCase
import com.example.travel_mate.domain.GetRemoteContributedTripsUseCase
import com.example.travel_mate.domain.GetRemoteTripsUseCase
import com.example.travel_mate.domain.GetSelectableContributorsUseCase
import com.example.travel_mate.domain.GetSelectedTripDataUseCase
import com.example.travel_mate.domain.GetUsersByUIDsUseCase
import com.example.travel_mate.domain.InitRouteUseCase
import com.example.travel_mate.domain.InitSearchAndRouteWithSelectedStartUseCase
import com.example.travel_mate.domain.InitSearchUseCase
import com.example.travel_mate.domain.LocationRepository
import com.example.travel_mate.domain.NavigateToNextPlaceUseCase
import com.example.travel_mate.domain.NavigationRepository
import com.example.travel_mate.domain.ProcessTripIdentifiersUseCase
import com.example.travel_mate.domain.RouteNodeRepository
import com.example.travel_mate.domain.RouteRepository
import com.example.travel_mate.domain.SaveLocalTripUseCase
import com.example.travel_mate.domain.SaveRemoteTripUseCase
import com.example.travel_mate.domain.SaveTripUseCase
import com.example.travel_mate.domain.SaveTripWithUpdatedPlacesUseCase
import com.example.travel_mate.domain.SearchAutocompleteUseCase
import com.example.travel_mate.domain.SearchOptionsRepository
import com.example.travel_mate.domain.SearchPlacesUseCase
import com.example.travel_mate.domain.SearchRepository
import com.example.travel_mate.domain.SearchReverseGeoCodeUseCase
import com.example.travel_mate.domain.SetCurrentTripContributorsUseCase
import com.example.travel_mate.domain.SetCustomPlaceUseCase
import com.example.travel_mate.domain.SetSearchMinuteUseCase
import com.example.travel_mate.domain.SetSearchTransportModeUseCase
import com.example.travel_mate.domain.SetUserPermissionUseCase
import com.example.travel_mate.domain.SignInUserUseCase
import com.example.travel_mate.domain.SignOutUserUseCase
import com.example.travel_mate.domain.SignUpUserUseCase
import com.example.travel_mate.domain.TripRepository
import com.example.travel_mate.domain.UpdateUserUseCase
import com.example.travel_mate.domain.UserRepository
import com.example.travel_mate.ui.ViewModelFactory
import com.example.travel_mate.ui.ViewModelMain
import com.example.travel_mate.ui.ViewModelUser
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

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

        val modules = module{

            single<PhotonRemoteDataSource> { PhotonRemoteDataSourceImpl() }
            singleOf(::LocationLocalDataSource)
            single<LocationRepository> { LocationRepositoryImpl() }
            single<RouteRemoteDataSource>{ RouteRemoteDataSourceImpl() }
            single<RouteNodeRepository>{ RouteNodeRepositoryImpl() }
            single<RouteRepository>{RouteRepositoryImpl()}
            single<OverpassRemoteDataSource> { OverpassRemoteDataSourceImpl() }
            single<RoomLocalDataSource> { RoomLocalDataSourceImpl() }
            single<FirebaseRemoteDataSource> { FirebaseRemoteDataSourceImpl() }
            single<FirebaseAuthenticationSource> { FirebaseAuthenticationSourceImpl() }
            single<SearchRepository> { SearchRepositoryImpl()}
            single<TripRepository> {TripRepositoryImpl()}
            single<CurrentTripRepository> { CurrentTripRepositoryImpl() }
            single<UserRepository>{UserRepositoryImpl()}
            single<NavigationRepository> { NavigationRepositoryImpl() }
            single<CustomPlaceRepository> { CustomPlaceRepositoryImpl() }
            single<SearchOptionsRepository> { SearchOptionsRepositoryImpl() }
            singleOf(::SearchPlacesUseCase)
            singleOf(::SetSearchTransportModeUseCase)
            singleOf(::SetSearchMinuteUseCase)
            singleOf(::NavigateToNextPlaceUseCase)
            singleOf(::SearchAutocompleteUseCase)
            singleOf(::InitSearchUseCase)
            singleOf(::InitRouteUseCase)
            singleOf(::SearchReverseGeoCodeUseCase)
            singleOf(::SetCustomPlaceUseCase)
            singleOf(::GetCurrentLocationUseCase)
            singleOf(::InitSearchAndRouteWithSelectedStartUseCase)
            singleOf(::InitSearchAndRouteWithLocationStartUseCase)
            singleOf(::UpdateUserUseCase)
            singleOf(::SignUpUserUseCase)
            singleOf(::SignInUserUseCase)
            singleOf(::SignOutUserUseCase)
            singleOf(::CheckUserUseCase)
            singleOf(::DeleteUserUseCase)
            singleOf(::SetUserPermissionUseCase)
            singleOf(::GetNewContributorDataUseCase)
            singleOf(::DeleteLocalTripUseCase)
            singleOf(::SaveRemoteTripUseCase)
            singleOf(::DeleteRemoteTripUseCase)
            singleOf(::SaveLocalTripUseCase)
            singleOf(::SaveTripUseCase)
            singleOf(::DeleteTripUseCase)
            singleOf(::GetLocalTripsUseCase)
            singleOf(::GetUsersByUIDsUseCase)
            singleOf(::ProcessTripIdentifiersUseCase)
            singleOf(::GetRemoteTripsUseCase)
            singleOf(::GetRemoteContributedTripsUseCase)
            singleOf(::GetSelectedTripDataUseCase)
            singleOf(::GetSelectableContributorsUseCase)
            singleOf(::SaveTripWithUpdatedPlacesUseCase)
            singleOf(::SetCurrentTripContributorsUseCase)

            singleOf(::ViewModelMain)
            singleOf(::ViewModelUser)

        }

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@Application)
            // Load modules
            modules(modules)
        }

        //done
/*        val photonRemoteDataSource = PhotonRemoteDataSourceImpl()

        //done
        val locationLocalDataSource = LocationLocalDataSource(
            appContext
        )
//done
        val locationRepository = LocationRepositoryImpl(
            locationLocalDataSource = locationLocalDataSource
        )
//done
        val routeRemoteDataSource = RouteRemoteDataSourceImpl()
//done
        val routeNodeRepository = RouteNodeRepositoryImpl(
            routeRemoteDataSource = routeRemoteDataSource
        )
//done
        val routeRepository = RouteRepositoryImpl(
            routeNodeRepository = routeNodeRepository
        )
//done
        val overpassRemoteDataSource = OverpassRemoteDataSourceImpl()
//done
        val roomLocalDataSource = RoomLocalDataSourceImpl(
            appDatabase = appDatabase
        )
//done
        val firebaseRemoteDataSource = FirebaseRemoteDataSourceImpl()
//done
        val firebaseAuthenticationSource = FirebaseAuthenticationSourceImpl()
//done
        val searchRepository = SearchRepositoryImpl(
            overpassRemoteDataSource = overpassRemoteDataSource,
            photonRemoteDataSource = photonRemoteDataSource,
            routeRemoteDataSource = routeRemoteDataSource
        )
//done
        val tripRepository = TripRepositoryImpl(
            roomLocalDataSource = roomLocalDataSource,
            firebaseRemoteDataSource = firebaseRemoteDataSource,
        )
//done        
        val currentTripRepository = CurrentTripRepositoryImpl()
//done        
        val userRepository = UserRepositoryImpl(
            firebaseAuthenticationSource = firebaseAuthenticationSource
        )
//done
        val navigationRepository = NavigationRepositoryImpl(
            routeNodeRepository = routeNodeRepository,
            locationRepository = locationRepository
        )
//done
        val customPlaceRepository = CustomPlaceRepositoryImpl(
            photonRemoteDataSource = photonRemoteDataSource
        )
//done
        val searchOptionsRepository = SearchOptionsRepositoryImpl()
//done
        val searchPlacesUseCase = SearchPlacesUseCase(
            searchRepository = searchRepository,
            searchOptionsRepository = searchOptionsRepository
        )
//done
        val setSearchTransportModeUseCase = SetSearchTransportModeUseCase(
            searchOptionsRepository = searchOptionsRepository
        )
//done
        val setSearchMinuteUseCase = SetSearchMinuteUseCase(
            searchOptionsRepository = searchOptionsRepository
        )
//done
        val navigateToNextPlaceUseCase = NavigateToNextPlaceUseCase(
            routeRepository = routeRepository,
            navigationRepository = navigationRepository
        )
//done
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

        val initSearchAndRouteWithSelectedStartUseCase = InitSearchAndRouteWithSelectedStartUseCase(
            initSearchUseCase = initSearchUseCase,
            initRouteUseCase = initRouteUseCase
        )

        val initSearchAndRouteWithLocationStartUseCase = InitSearchAndRouteWithLocationStartUseCase(
            getCurrentLocationUseCase = getCurrentLocationUseCase,
            searchReverseGeoCodeUseCase = searchReverseGeoCodeUseCase,
            initSearchUseCase = initSearchUseCase,
            initRouteUseCase = initRouteUseCase
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
            initSearchAndRouteWithSelectedStartUseCase = initSearchAndRouteWithSelectedStartUseCase,
            setCustomPlaceUseCase = setCustomPlaceUseCase,
            initSearchAndRouteWithLocationStartUseCase = initSearchAndRouteWithLocationStartUseCase,
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
        )*/
    }

}
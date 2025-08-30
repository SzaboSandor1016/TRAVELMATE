package com.example.travel_mate

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.core.database.data.datasource.RoomLocalDataSourceImpl
import com.example.core.database.domain.datasource.RoomLocalDataSource
import com.example.app.location.data.datasource.LocationLocalDataSourceImpl
import com.example.app.location.data.repository.LocationRepositoryImpl
import com.example.core.database.AppDatabase
import com.example.di.authModule
import com.example.di.findCustomModule
import com.example.di.inspectModule
import com.example.di.navigationModule
import com.example.di.reverseGeoCodeModule
import com.example.di.routeDataSourceModule
import com.example.di.routeModule
import com.example.di.saveTripModule
import com.example.di.searchModule
import com.example.di.searchPlacesDataSourceModule
import com.example.di.searchStartDataSourceModule
import com.example.di.selectedPlaceModule
import com.example.di.selectedPlaceOptionsModule
import com.example.di.tripRemoteDataSourceModule
import com.example.di.tripsModule
import com.example.di.userModule
import com.example.app.location.domain.datasource.LocationLocalDataSource
import com.example.app.location.domain.repository.LocationRepository
import com.example.app.location.domain.usecases.GetCurrentLocationUseCase
import com.example.di.locationModule
import com.example.navigation.NavigateToOuterDestinationUseCase
import com.example.navigation.OuterNavigator
import com.example.travel_mate.ui.viewmodel.ViewModelMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
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
        //lateinit var factory: ViewModelFactory

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

        val databaseModule = module {

            single<RoomLocalDataSource> { RoomLocalDataSourceImpl(appDatabase.tripDao()) }
        }

        val appModule =  module {

            single(named("ApplicationScope")) {
                CoroutineScope(SupervisorJob() + Dispatchers.Default)
            }

            singleOf(::ViewModelMain)
        }

        val appNavigationModule = module {


            single<OuterNavigator> { (activity: ActivityMain) -> activity }

            factory { (outerNavigator: OuterNavigator) ->
                NavigateToOuterDestinationUseCase(outerNavigator)
            }
        }

        /*val modules = module{

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

        }*/

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@Application)
            // Load modules
            modules(
                appNavigationModule,
                authModule,
                databaseModule,
                findCustomModule,
                inspectModule,
                locationModule,
                navigationModule,
                reverseGeoCodeModule,
                routeDataSourceModule,
                routeModule,
                saveTripModule,
                searchModule,
                searchPlacesDataSourceModule,
                searchStartDataSourceModule,
                selectedPlaceModule,
                selectedPlaceOptionsModule,
                tripRemoteDataSourceModule,
                tripsModule,
                userModule,
                appModule
            )
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

    /*
    For Flows: mapSafe / mapSafeOrPrevious
    fun <T> Flow<T?>.mapSafeOrPrevious(): Flow<T?> = flow {
    var lastValue: T? = null
    collect { value ->
        val emitValue = value ?: lastValue
        emit(emitValue)
        if (value != null) lastValue = value
    }
    }
    Usage:
    val usernameFlow: Flow<String?> = firebaseAuthSource.userFlow()
    .map { it?.uid?.let { uid -> firebaseAuthSource.getUsernameByUID(uid) } }
    .mapSafeOrPrevious()

    If the fetch returns null, it emits the previous known value.

If the fetch succeeds, it updates lastValue.

Useful for network calls or DB lookups that may temporarily fail.

2. For suspend functions

If you just need a one-off call and fallback to a previous value:

suspend fun <T> safeOrPrevious(
    previous: T?,
    block: suspend () -> T?
): T? = try {
    block() ?: previous
} catch (e: Exception) {
    previous
}
Usage:
val previousUsername: String? = ...
val username = safeOrPrevious(previousUsername) {
    firebaseAuthSource.getUsernameByUID(userId)
}
If getUsernameByUID returns null or throws, it returns previousUsername.

Keeps your state consistent without crashing.
    */

}
package com.example.travel_mate

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.core.database.data.datasource.RoomLocalDataSourceImpl
import com.example.core.database.domain.datasource.RoomLocalDataSource
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

class Application: Application() {

    companion object {
        lateinit var appContext: Context
            private set

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
    }
}
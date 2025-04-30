package com.example.travel_mate

import android.app.Application
import android.content.Context
import androidx.room.Room

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

        val routeRepository = RouteRepository(
            RouteRemoteDataSourceImpl(),
            LocationLocalDataSource(appContext)
        )

        val searchRepository = SearchRepository(
            OverpassRemoteDataSourceImpl(),
            PhotonRemoteDataSourceImpl(),
            LocationLocalDataSource(appContext))

        val tripRepository = TripRepository(
            RoomLocalDataSourceImpl(appDatabase),
            FirebaseAuthenticationSourceImpl(),
            FirebaseRemoteDataSourceImpl())

        factory = ViewModelFactory(
            tripRepository = tripRepository,
            searchRepository = searchRepository,
            routeRepository = routeRepository)
    }

}
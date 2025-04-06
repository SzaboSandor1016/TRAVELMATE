package com.example.travel_mate

import android.app.Application
import android.content.Context
import androidx.room.Room

/** [MyApplication]
 *  custom [Application]
 *  that creates a [ViewModelFactory] for the app's [ViewModel]s
 *  and builds the [Room] [AppDatabase]
 */
class MyApplication: Application() {

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

        val searchRepository = SearchRepository(OverpassRemoteDataSourceImpl(), PhotonRemoteDataSourceImpl(),
            RouteRemoteDataSourceImpl())
        val tripRepository = TripRepository(RoomLocalDataSourceImpl(appDatabase),FirebaseAuthenticationSourceImpl(),FirebaseRemoteDataSourceImpl())

        factory = ViewModelFactory(tripRepository,searchRepository)
    }

}
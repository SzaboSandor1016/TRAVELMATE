package com.example.features.trips.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.core.auth.domain.FirebaseAuthenticationSource
import com.example.core.database.domain.datasource.RoomLocalDataSource
import com.example.core.remotedatasources.tripremotedatasource.domain.datasource.FirebaseRemoteDataSource
import com.example.core.remotedatasources.tripremotedatasource.domain.models.TripIdentifierRemoteEntityModel
import com.example.features.trips.data.mappers.toContributorTripDomainModel
import com.example.features.trips.data.mappers.toTripIdentifierTripsDomainModel
import com.example.features.trips.data.mappers.toTripTripsDomainModel
import com.example.features.trips.domain.models.TripIdentifierTripsDomainModel
import com.example.features.trips.domain.models.TripTripsDomainModel
import com.example.features.trips.domain.repositories.TripRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class TripRepositoryImpl(
    private val appScope: CoroutineScope
): TripRepository {

    private val roomLocalDataSource: RoomLocalDataSource by inject(RoomLocalDataSource::class.java)
    private val firebaseRemoteDataSource: FirebaseRemoteDataSource by inject(
        FirebaseRemoteDataSource::class.java
    )
    private val firebaseAuthenticationSource: FirebaseAuthenticationSource by inject(
        FirebaseAuthenticationSource::class.java
    )

    private val tripCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO

    @OptIn(ExperimentalCoroutinesApi::class)
    private val userState = firebaseAuthenticationSource.userFlow().mapLatest {

        it
    }.flowOn(
        Dispatchers.Main
    ).stateIn(
        appScope,
        SharingStarted.Eagerly,
        null
    )

    override fun hasUserSignedIn(): Flow<Boolean> {
        return userState.map {
            it != null
        }
    }

    override fun getCurrentUserId(): Flow<String?> {

        return userState.map {
            it?.uid
        }
    }

    override suspend fun deleteCurrentTripFromRemoteDatabase(tripUuid: String) {

        withContext(tripCoroutineDispatcher) {

            firebaseRemoteDataSource.deleteTrip(
                uuid = tripUuid
            )
        }
    }

    override suspend fun deleteUidFromContributedTrips(uid: String, tripUuid: String) {

        withContext(tripCoroutineDispatcher) {

            firebaseRemoteDataSource.deleteUidFromContributedTrips(
                uid = uid,
                tripUUID = tripUuid
            )
        }
    }

    override suspend fun getCurrentRemoteTripData(
        remoteTripUUID: String
    ): Flow<TripTripsDomainModel> {

        return withContext(tripCoroutineDispatcher) {

            flowOf(
                firebaseRemoteDataSource.findTripById(
                    uuid = remoteTripUUID
                )!!.toTripTripsDomainModel()
            )
        }
    }

    override suspend fun fetchMyTripsFromFirebase(): Flow<List<TripIdentifierTripsDomainModel.Remote>> {

        val userID = userState.value?.uid

        return if (userID != null) {

            flowOf(firebaseRemoteDataSource.fetchMyTrips(uid = userID).map {

                processRemoteTripIdentifier(userUid = userID, it)
            })
        } else {
            flowOf(emptyList())
        }
    }

    override suspend fun fetchContributedTripsFromFirebase(): Flow<List<TripIdentifierTripsDomainModel.Remote>> {

        val userID = userState.value?.uid

        return if (userID != null) {

            flowOf(
                firebaseRemoteDataSource.fetchContributedTrips(
                    uid = userID
                ).map { processRemoteTripIdentifier(userID, it) }
            )
        } else {

            flowOf(emptyList())
        }
    }

    override suspend fun deleteCurrentTripFromLocalDatabase(
        tripUID: String
    ) {
        withContext(tripCoroutineDispatcher) {

            roomLocalDataSource.deleteTrip(
                uuid = tripUID
            )
        }
    }

    override suspend fun getCurrentLocalTripData(
        localTripUUID: String
    ): TripTripsDomainModel.Local {

        return withContext(tripCoroutineDispatcher) {

            return@withContext roomLocalDataSource.findTripById(
                uuid = localTripUUID
            ).toTripTripsDomainModel()
        }
    }

    override suspend fun fetchAllLocalSavedTrips(): Flow<List<TripIdentifierTripsDomainModel.Local>> {

        return withContext(tripCoroutineDispatcher) {

            roomLocalDataSource.fetchTripIdentifiers().map { tripIds ->
                tripIds.map {
                    it.toTripIdentifierTripsDomainModel()
                }
            }
        }
    }

    suspend fun processRemoteTripIdentifier(
        userUid: String,
        entity: TripIdentifierRemoteEntityModel
    ): TripIdentifierTripsDomainModel.Remote {

        val creator = firebaseAuthenticationSource.findUserByUidFromUsernameToUid(
            entity.creatorUID!!
        )!!

        val permission =
            if (userUid == entity.creatorUID) true else entity.contributors.any { (it.key == userUid && it.value.canUpdate) }

        val contributors = entity.contributors.mapValues {

            val contributor =
                firebaseAuthenticationSource.findUserByUidFromUsernameToUid(
                    it.key
                )!!

            it.value.toContributorTripDomainModel(
                contributor.component1(),
                contributor.component2()
            )
        }

        return entity.toTripIdentifierTripsDomainModel(
            creatorUsername = creator.component2(),
            contributors = contributors,
            permission = permission
        )
    }
}
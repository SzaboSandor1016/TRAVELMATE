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

    /*companion object {
        private const val SAVED_TRIPS_FILE_NAME = "saved_trips.json"
        private val TRIP_CLASS_TYPE = TripTr::class.java
    }*/

    private val tripCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO

    //private val _tripsStateFlow = MutableStateFlow(TripsState())
    //override val tripsStateFlow: StateFlow<TripsState> = _tripsStateFlow.asStateFlow()

    private val _writeErrorMessage = MutableLiveData<String?>()
    val writeErrorMessage: LiveData<String?> = _writeErrorMessage

    private val _readErrorMessage = MutableLiveData<String?>()
    val readErrorMessage: LiveData<String?> = _readErrorMessage

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

    /*override suspend fun deleteUserTripsFromRemoteDatabase(userUid: String) {

        firebaseRemoteDataSource.deleteTripsByUserUid(
            uid = userUid
        )
    }*/

    /*suspend fun resetCurrentTrip() {


        withContext(Dispatchers.IO) {

            val newTrip = Trip()

            _currentTripState.update {

                it.copy(
                    trip = newTrip,
                    tripIdentifier = TripIdentifier(
                        uuid = newTrip.uUID
                    )
                )
            }
        }
    }*/

    /*override suspend fun saveNewTrip(trip: Trip, tripIdentifier: TripIdentifier) {

        withContext(tripCoroutineDispatcher) {

            *//*when (tripIdentifier.contributors.isEmpty()) {

                true -> {

                    uploadTripToLocalDatabase(
                        trip = trip
                    )

                    if (tripIdentifier.location.equals("remote")) {
                        deleteCurrentTripFromRemoteDatabase(trip)
                    }
                }

                false -> {

                    uploadTripToRemoteDatabase(
                        trip = trip,
                        tripIdentifier = tripIdentifier
                    )

                    if (tripIdentifier.location.equals("local")) {
                        deleteCurrentTripFromLocalDatabase(trip)
                    }
                }
            }*//*
        }
    }*/

    /*override suspend fun saveTripWithUpdatedPlaces(startPlace: Place, places: List<Place>) {

        withContext(tripCoroutineDispatcher) {

            val newTrip = _currentTripState.value.trip?

            val tripIdentifier = _currentTripState.value.tripIdentifier

            saveNewTrip(
                trip = newTrip!!,
                tripIdentifier = tripIdentifier!!
            )

            *//*_currentTripState.update {

                it.copy(
                    trip = newTrip,
                    tripIdentifier = tripIdentifier
                )
            }*//*
        }

    }*/

    /*//todo handle when statement in usecase
    override suspend fun deleteCurrentTrip(trip: Trip, tripIdentifier: TripIdentifier) {

        withContext(tripCoroutineDispatcher) {

            *//*when( tripIdentifier.location) {

                "local" -> { deleteCurrentTripFromLocalDatabase(trip) }
                "remote" -> { deleteCurrentTripFromRemoteDatabase(trip) }
            }*//*
        }
    }*/

    /*suspend fun updateCurrentTripData(trip: Trip,tripIdentifier: TripIdentifier) {

        withContext(Dispatchers.IO) {

            _currentTripState.update {
                it.copy(
                    trip = trip,
                    tripIdentifier = tripIdentifier
                )
            }
        }

    }*/
//todo handle when statement in usecase
    /*suspend fun getCurrentTripData(tripIdentifier: TripIdentifier) {

        withContext(tripCoroutineDispatcher) {

            when (tripIdentifier.location) {

                "local" -> {
                    getCurrentLocalTripData(
                        tripIdentifier = tripIdentifier
                    )
                }

                "remote" -> {
                    getCurrentRemoteTripData(
                        tripIdentifier = tripIdentifier
                    )
                }
            }
        }
    }*/

    /*suspend fun initNewCurrentTripState(startPlace: Place, places: List<Place>) {

        withContext(Dispatchers.IO) {

            _currentTripState.update {
                it.copy(
                    trip = it.trip.copy(
                        startPlace = startPlace,
                        places = places
                    )
                )
            }
        }
    }

    suspend fun setCurrentTripStateTitle(title: String) {

        withContext(Dispatchers.IO) {

            _currentTripState.update {
                it.copy(
                    trip = it.trip.copy(
                        title = title
                    ),
                    tripIdentifier = it.tripIdentifier.apply {

                        this.title = title
                    }
                )
            }
        }
    }

    suspend fun setCurrentTripStateDate(date: String) {

        withContext(Dispatchers.IO) {

            _currentTripState.update {
                it.copy(
                    trip = it.trip.copy(

                        date = date
                    )
                )
            }
        }
    }

    suspend fun setCurrentTripStateNote(note: String) {

        withContext(Dispatchers.IO) {

            _currentTripState.update {
                it.copy(
                    trip = it.trip.copy(
                        note = note
                    )
                )
            }
        }
    }*/

    /*suspend fun setCurrentTripStateContributors(contributors: Map<String,String>): Map<> {

        withContext(Dispatchers.IO) {

            _currentTripState.update {
                it.copy(
                    tripIdentifier = it.tripIdentifier.copy(
                        contributors = contributors.mapValues { true }
                    )
                )
            }
        }
    }*/


    /**FirebaseDatabase methods block
     * -------------------------
     * Methods related to storing [com.example.travel_mate.data.Trip]s in the Firebase realtime database
     * [fetchMyTripsFromFirebase] fetches the trips uploaded by the currently logged-in user
     * [fetchContributedTripsFromFirebase] fetches the trips to which the current user is added as a contributor
     * */
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF FIREBASE DATABASE MANAGEMENT METHODS
    //----------------------------------------------------------------------------------------------------------------

    //todo reminder handle the if statement in the responsible UseCase
    //if (_currentTripState.value.tripIdentifier?.creatorUID == firebaseUser?.uid) {
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

    //TODo map to TripTripsDomainModel.Remote
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
            /*val currentTrip = async {

            }*/

            /*Log.d("FirebaseDatabaseUID", tripIdentifier.uuid.toString())
            Log.d("FirebaseDatabaseTitle", tripIdentifier.title.toString())
            Log.d("FirebaseDatabaseLocation", tripIdentifier.location.toString())
            Log.d("FirebaseDatabaseCreator", tripIdentifier.creatorUID.toString())
            *//*Log.d("FirebaseDatabaseContributorUID", tripIdentifier.contributors.map { it.key }.toString())*//*

            *//*updateCurrentTripData(
                trip = currentTrip.await(),
                tripIdentifier = tripIdentifier
            )*//*

            _currentTripState.update {
                it.copy(
                    tripIdentifier = tripIdentifier
                )
            }

            _currentTripState.update {
                it.copy(
                    trip = currentTrip.await()
                )
            }*/

/*        }
    }*/

    /** [fetchMyTripsFromFirebase]
     *  fetches trips uploaded by the current user
     *  then updates the [_tripsStateFlow] with the received data
     * */
    override suspend fun fetchMyTripsFromFirebase(
        //userUid: String
    ): Flow<List<TripIdentifierTripsDomainModel.Remote>> {

        val userID = userState.value?.uid

        return if (userID != null) {

            flowOf(firebaseRemoteDataSource.fetchMyTrips(uid = userID).map {

                processRemoteTripIdentifier(userUid = userID, it)
            })
        }else {
            flowOf(emptyList())
        }
    }
            /*if (firebaseUser != null) {


                _tripsStateFlow.update {
                    it.copy(
                        trips = processFetchedTrips(
                            tripIdentifiers = myTrips
                        )
                    )
                }
            }*/
        //}
    //}

    /** [fetchContributedTripsFromFirebase]
     *  fetches trips that has the current user's UID among the contributors
     *  then updates the [_tripsStateFlow] with the received data
     */
    override suspend fun fetchContributedTripsFromFirebase(
        //userUid: String
    ): Flow<List<TripIdentifierTripsDomainModel.Remote>> {

        val userID = userState.value?.uid

        return if (userID != null) {

            flowOf(
                firebaseRemoteDataSource.fetchContributedTrips(
                    uid = userID
                ).map { processRemoteTripIdentifier(userID, it) }
            )
        }else {

            flowOf(emptyList())
        }

        /*return withContext(tripCoroutineDispatcher) {

            flowOf(

            )

            *//*if (firebaseUser != null) {



                _tripsStateFlow.update {
                    it.copy(
                        trips = processFetchedTrips(
                            tripIdentifiers = contributedTrips
                        )
                    )
                }
            }*//*
        }*/
    }


    //----------------------------------------------------------------------------------------------------------------
    //END OF FIREBASE DATABASE MANAGEMENT METHODS
    //----------------------------------------------------------------------------------------------------------------

    /**Local save methods block
     * -------------------------
     * Methods related to saving trips that does not have contributors to the local storage of the device
     * -Write trips to the local storage [uploadTripToLocalDatabase]
     * -Fetch trips from the local storage [fetchAllLocalSavedTrips]
     * -Get trip by its UUID [getCurrentLocalTripData]
     * To be implemented:
     * --Update specific properties of a trip
     * --Delete a trip specified by its UUID
     * */
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF LOCALLY SAVED TRIPS MANAGEMENT METHODS
    //----------------------------------------------------------------------------------------------------------------

    /** [getCurrentLocalTripData]
     * returns the [com.example.travel_mate.data.Trip] object with the uuid attribute equal to the one given as the parameter of the function
     * then updates the [_currentTripState] with the result
     */

    override suspend fun deleteCurrentTripFromLocalDatabase(
        tripUID: String
    ) {
        withContext(tripCoroutineDispatcher) {

            roomLocalDataSource.deleteTrip(
                uuid = tripUID
            )

            //resetCurrentTrip()
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
        /*return withContext(tripCoroutineDispatcher) {


        }*/

    //}

    /** [fetchAllLocalSavedTrips]
     *  fetches all [com.example.travel_mate.data.Trip]s stored in the device's local storage
     *  then updates the [_tripsStateFlow] with the returned data
     */
    override suspend fun fetchAllLocalSavedTrips(): Flow<List<TripIdentifierTripsDomainModel.Local>> {

        return withContext(tripCoroutineDispatcher) {

            roomLocalDataSource.fetchTripIdentifiers().map { tripIds ->
                tripIds.map {
                    it.toTripIdentifierTripsDomainModel()
                }
            }

                /*_tripsStateFlow.update {

                    it.copy(
                        trips = trips.await()
                    )
                }*/
        }
    }
    //----------------------------------------------------------------------------------------------------------------
    //END OF LOCALLY SAVED TRIPS MANAGEMENT METHODS
    //----------------------------------------------------------------------------------------------------------------


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

    /** State holder data class definitions
     * -----------------------------------
     * Definitions of data classes that store information related to the data have to be shown on screen
     * [TripsState] class for holding the information of the [com.example.travel_mate.ui.FragmentUser]'s UI
     * [User] class for holding the relevant information of the currently logged-in user
     * */

    //data class TripsState(val trips: List<TripIdentifier> = emptyList())

    /*@IgnoreExtraProperties
    data class TripIdentifier(
        @get:Exclude var uuid: String? = null,
        @get:Exclude var location: String? = null,
        var title: String? = null,
        @Ignore var contributorUIDs: Map<String, Boolean> = emptyMap(),
        @Ignore var contributors: Map<String, Contributor> = emptyMap(),
        @Ignore
        @get:Exclude var creatorUsername: String? = null,
        @Ignore
        @get:Exclude val permissionToUpdate: Boolean = true,
        var creatorUID: String? = null
    ): Serializable {

        constructor(uuid: String?, title: String?) : this() {
            this.uuid = uuid
            this.title = title
        }
    }*/

    //   suspend fun getSelectableContributors() {
    //
    //        withContext(Dispatchers.IO) {
    //
    //            val contributorsOfTrip = _currentTripState.value.tripIdentifier?.contributors
    //
    //            val recentContributorsOfUser = getRecentContributorsOfUser().map {
    //
    //                Pair(it.key,
    //                    Contributor(
    //                        uid = it.key,
    //                        username = it.value,
    //                        selected = false
    //                    )
    //                )
    //            }.toMap()
    //
    //            var fullContributors: MutableMap<String,Contributor> = mutableMapOf()
    //
    //            fullContributors.putAll(contributorsOfTrip!!)
    //
    //            fullContributors.putAll(recentContributorsOfUser.filter {
    //                !fullContributors.map { it.value.username.toString() }.contains(it.value.username.toString())
    //            })
    //
    //            _userState.update {
    //                it.copy(
    //
    //                    contributors = fullContributors
    //                )
    //            }
    //        }
    //
    //    }
}
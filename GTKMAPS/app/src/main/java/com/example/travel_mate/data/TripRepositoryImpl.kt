package com.example.travel_mate.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Ignore
import com.example.travel_mate.domain.TripRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import java.io.Serializable

class TripRepositoryImpl: TripRepository {

    private val roomLocalDataSource: RoomLocalDataSource by inject(RoomLocalDataSource::class.java)
    private val firebaseRemoteDataSource: FirebaseRemoteDataSource by inject(FirebaseRemoteDataSource::class.java)

    companion object {
        private const val SAVED_TRIPS_FILE_NAME = "saved_trips.json"
        private val TRIP_CLASS_TYPE = Trip::class.java
    }

    private val tripCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO

    //private val _tripsStateFlow = MutableStateFlow(TripsState())
    //override val tripsStateFlow: StateFlow<TripsState> = _tripsStateFlow.asStateFlow()

    private val _writeErrorMessage = MutableLiveData<String?>()
    val writeErrorMessage: LiveData<String?> = _writeErrorMessage

    private val _readErrorMessage = MutableLiveData<String?>()
    val readErrorMessage: LiveData<String?> = _readErrorMessage


    override suspend fun deleteUserTripsFromRemoteDatabase(userUid: String) {

        firebaseRemoteDataSource.deleteTripsByUserUid(
            uid = userUid
        )
    }

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
     * Methods related to storing [Trip]s in the Firebase realtime database
     * [fetchMyTripsFromFirebase] fetches the trips uploaded by the currently logged-in user
     * [fetchContributedTripsFromFirebase] fetches the trips to which the current user is added as a contributor
     * */
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF FIREBASE DATABASE MANAGEMENT METHODS
    //----------------------------------------------------------------------------------------------------------------

    //todo reminder handle tripIdentifier null check in the UseCase
    //when (tripIdentifier.creatorUID) {
    //
    //                null -> firebaseRemoteDataSource.uploadTrip(
    //                    trip = trip,
    //                    firebaseIdentifier = tripIdentifier.copy(
    //                        creatorUID = firebaseUser!!.uid
    //                    )
    //                )
    //else -> firebaseRemoteDataSource.uploadTrip(
    //                    trip = trip,
    //                    firebaseIdentifier = tripIdentifier
    //                )
    override suspend fun uploadTripToRemoteDatabase(userUid: String, trip: Trip, tripIdentifier: TripIdentifier) {

        withContext(tripCoroutineDispatcher) {

                firebaseRemoteDataSource.uploadTrip(
                    trip = trip,
                    firebaseIdentifier = tripIdentifier.copy(
                        creatorUID = userUid
                    )
                )
        }
    }

    override suspend fun uploadContributedTripToRemoteDatabase(trip: Trip, tripIdentifier: TripIdentifier) {

        withContext(tripCoroutineDispatcher) {

            firebaseRemoteDataSource.uploadTrip(
                trip = trip,
                firebaseIdentifier = tripIdentifier
            )
        }
    }

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

    override suspend fun getCurrentRemoteTripData(tripIdentifier: TripIdentifier ): Flow<Trip> {

        return withContext(tripCoroutineDispatcher) {

            return@withContext flowOf(firebaseRemoteDataSource.findTripById(
                uuid = tripIdentifier.uuid.toString()
            ))

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

        }
    }

    /** [fetchMyTripsFromFirebase]
     *  fetches trips uploaded by the current user
     *  then updates the [_tripsStateFlow] with the received data
     * */
    override suspend fun fetchMyTripsFromFirebase(firebaseUser: FirebaseUser): Flow<List<TripIdentifier>> {

        return withContext(tripCoroutineDispatcher) {

            return@withContext flowOf(firebaseRemoteDataSource.fetchMyTrips(uid = firebaseUser.uid)
                .filterNotNull())

            /*if (firebaseUser != null) {


                _tripsStateFlow.update {
                    it.copy(
                        trips = processFetchedTrips(
                            tripIdentifiers = myTrips
                        )
                    )
                }
            }*/
        }
    }

    /** [fetchContributedTripsFromFirebase]
     *  fetches trips that has the current user's UID among the contributors
     *  then updates the [_tripsStateFlow] with the received data
     */
    override suspend fun fetchContributedTripsFromFirebase(userUid: String): Flow<List<TripIdentifier>> {

        return withContext(tripCoroutineDispatcher) {

            return@withContext flowOf(firebaseRemoteDataSource.fetchContributedTrips(
                uid = userUid
            ).filterNotNull())

            /*if (firebaseUser != null) {



                _tripsStateFlow.update {
                    it.copy(
                        trips = processFetchedTrips(
                            tripIdentifiers = contributedTrips
                        )
                    )
                }
            }*/
        }
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
     * returns the [Trip] object with the uuid attribute equal to the one given as the parameter of the function
     * then updates the [_currentTripState] with the result
     */

    override suspend fun deleteCurrentTripFromLocalDatabase(trip: Trip) {
        withContext(tripCoroutineDispatcher) {

            roomLocalDataSource.deleteTrip(
                trip = trip
            )

            //resetCurrentTrip()
        }
    }

    override suspend fun getCurrentLocalTripData(tripIdentifier: TripIdentifier): Flow<Trip> {

        return withContext(tripCoroutineDispatcher) {

            return@withContext flowOf(
                roomLocalDataSource.findTripById(
                    uuid = tripIdentifier.uuid.toString()
                )
            )
        }

    }

    /** [fetchAllLocalSavedTrips]
     *  fetches all [Trip]s stored in the device's local storage
     *  then updates the [_tripsStateFlow] with the returned data
     */
    override suspend fun fetchAllLocalSavedTrips(): Flow<List<TripIdentifier>> {

        return withContext(tripCoroutineDispatcher) {

                return@withContext flowOf(roomLocalDataSource.fetchTripIdentifiers())

                /*_tripsStateFlow.update {

                    it.copy(
                        trips = trips.await()
                    )
                }*/
        }
    }

    /** [uploadTripToLocalDatabase]
     *  writes the list of [Trip]s passed as the [tripToUpload] parameter of this function to the device's
     *  local storage
     */
    override suspend fun uploadTripToLocalDatabase(trip: Trip){

        withContext(tripCoroutineDispatcher) {

            roomLocalDataSource.uploadTrip(
                trip = trip
            )
        }
    }
    //----------------------------------------------------------------------------------------------------------------
    //END OF LOCALLY SAVED TRIPS MANAGEMENT METHODS
    //----------------------------------------------------------------------------------------------------------------


    /** State holder data class definitions
     * -----------------------------------
     * Definitions of data classes that store information related to the data have to be shown on screen
     * [TripsState] class for holding the information of the [com.example.travel_mate.ui.FragmentUser]'s UI
     * [User] class for holding the relevant information of the currently logged-in user
     * */

    //data class TripsState(val trips: List<TripIdentifier> = emptyList())

    @IgnoreExtraProperties
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
    }

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
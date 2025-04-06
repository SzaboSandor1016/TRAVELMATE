package com.example.travel_mate

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Ignore
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable
import kotlin.coroutines.coroutineContext

class TripRepository constructor(
    private val roomLocalDataSource: RoomLocalDataSource,
    private val authenticationSource: FirebaseAuthenticationSource,
    private val firebaseRemoteDataSource: FirebaseRemoteDataSource
) {

    companion object {
        private const val SAVED_TRIPS_FILE_NAME = "saved_trips.json"
        private val TRIP_CLASS_TYPE = Trip::class.java
    }
    private var firebaseUser: FirebaseUser? = null

    private val _tripsStateFlow = MutableStateFlow(TripsState())
    val tripsStateFlow: StateFlow<TripsState> = _tripsStateFlow.asStateFlow()

    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private val _currentTripState = MutableStateFlow(CurrentTrip())
    val currentTripState: StateFlow<CurrentTrip> = _currentTripState.asStateFlow()

    private val _writeErrorMessage = MutableLiveData<String?>()
    val writeErrorMessage: LiveData<String?> = _writeErrorMessage

    private val _readErrorMessage = MutableLiveData<String?>()
    val readErrorMessage: LiveData<String?> = _readErrorMessage

    /**FirebaseAuth methods block
     *-------------------------
     * Methods related to managing the firebase account of the current user on the local device
     * Check if there is a user currently signed in [checkUser]
     * -Create user [createUser]
     * -Log in to the account [signIn]
     * -Log out of the account [signOut]
     * To be implemented:
     * --Delete the account
     * --Change the password of the account
     **/
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF FIREBASE USER MANAGEMENT METHODS
    //----------------------------------------------------------------------------------------------------------------

    /** [checkUser]
     *  calls the [authenticationSource]'s [checkUser] function to find out
     *  if there is a user currently signed in
     *  then if there is, updates the [UserState] with the user's username
     *  and updates the value of the [firebaseUser] with the returned [FirebaseUser]
     */
    suspend fun checkUser() {

        withContext(Dispatchers.IO) {

            try {
                val user = async { authenticationSource.checkUser() }

                //Log.d("FirebaseCurrentUser", user.await()!!.uid)

                getUsernameAndUpdateUserState(user.await())
            } catch (e: Exception) {

                Log.e("FirebaseCurrentUser", "error getting the currently signed in user", e)
            }

        }
    }

    /** [createUser]
     * Calls the [createUser] function of the [authenticationSource]
     * then updates the [_tripsStateFlow] with the returned [FirebaseUser]
     */
    suspend fun createUser(email: String, password: String, username: String ) {

        withContext(Dispatchers.IO) {

            try {
                val user = authenticationSource.createUser(
                    email = email,
                    password = password,
                    username = username
                )

                getUsernameAndUpdateUserState(user)
            } catch (e: Exception) {
                Log.e("FirebaseCurrentUser", "error with creating firebase user", e)
            }

        }
    }

    /** [signIn]
     *  Signs the user in to firebase at the [authenticationSource]'s [signIn] method
     *  with the given [email] and [password] parameters
     *  then updates the [_tripsStateFlow] with the returned [FirebaseUser]
     */
    suspend fun signIn(email: String, password: String) {

        withContext(Dispatchers.IO) {

            try {

                val user = authenticationSource.signIn(
                    email = email,
                    password = password
                )

                getUsernameAndUpdateUserState(user)
            } catch (e: Exception) {

                Log.e("FirebaseCurrentUser", "error with sign in to firebase", e)
            }
        }
    }

    /** [signOut]
     *  Signs the user out at the [authenticationSource]'s [signOut] method
     *  then updates the [_tripsStateFlow] with a new (default) [User]
     */
    suspend fun signOut() {

        withContext(Dispatchers.IO) {

            val user = authenticationSource.signOut()

            firebaseUser = user

            _userState.update {
                it.copy(
                    username = null
                )
            }
        }
    }

    private suspend fun getUsernameAndUpdateUserState(user: FirebaseUser?) {

        withContext(Dispatchers.IO) {

            if (user != null) {

                firebaseUser = user

                Log.d("FirebaseCurrentUserGlobal", firebaseUser!!.uid)

                try {
                    val usernameByUid = authenticationSource.getUsernameByUID(user.uid)

                    Log.d("FirebaseCurrentUser", usernameByUid.toString())

                    _userState.update {
                        it.copy(
                            username = usernameByUid
                        )
                    }

                } catch (e: Exception) {

                    Log.e("FirebaseCurrentUser", "Failed to get current user data: exception", e)

                    _userState.update {
                        it.copy(
                            username = null
                        )
                    }
                }

            } else {
                _userState.update {
                    it.copy(
                        username = null
                    )
                }
            }
        }
    }

    suspend fun deleteCurrentUser(password: String) {

        withContext(Dispatchers.IO) {

            val uid = firebaseUser?.uid

            authenticationSource.deleteUserData(
                uid = uid.toString()
            )
            authenticationSource.deleteUserUsernameUIDPair(
                uid = uid.toString()
            )
            authenticationSource.deleteUserUIDFromRecentContributors(
                uid = uid.toString()
            )
            firebaseRemoteDataSource.deleteTripsByUserUid(
                uid = uid.toString()
            )
            authenticationSource.deleteUser(
                password = password
            )

            checkUser()

        }

    }

    suspend fun resetPassword(email: String) {

        withContext(Dispatchers.IO) {

            authenticationSource.resetPassword(
                email = email
            )
        }

    }

    suspend fun changePassword(currentPassword: String, newPassword: String) {

        withContext(Dispatchers.IO) {

            authenticationSource.changePassword(
                currentPassword = currentPassword,
                newPassword = newPassword
            )

            checkUser()

        }
    }

    //----------------------------------------------------------------------------------------------------------------
    //END OF FIREBASE USER MANAGEMENT METHODS
    //----------------------------------------------------------------------------------------------------------------

    /** Firebase "other users methods" block
     * -------------------------
     * Functions for finding other users that contributed to the trips of the user currently signed in
     * or finding other users by their usernames
     */
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF "OTHER USERS METHODS" BLOCK
    //----------------------------------------------------------------------------------------------------------------

    suspend fun findUserByUsername(username: String): Pair<String,String>? {

        return withContext(Dispatchers.IO) {

            return@withContext authenticationSource.findUserByUsername(
                username = username
            )
        }
    }

    suspend fun getUserPairs(usernames: List<String>): Map<String,String> {

        return withContext(Dispatchers.IO) {

            return@withContext authenticationSource.getUserPairsByUsernames(
                usernames = usernames
            )
        }
    }

    suspend fun setRecentContributors(users: Map<String, Boolean>) {

        withContext(Dispatchers.IO) {

            authenticationSource.setRecentContributors(
                uid = firebaseUser!!.uid,
                users = users
            )
        }
    }

    suspend fun getRecentContributorsOfUser(): Map<String, String> {

        return withContext(Dispatchers.IO) {

            val deferredList = async { authenticationSource.getRecentContributorsOfUser(firebaseUser!!.uid) }

            val contributors = deferredList.await()

            if (contributors.isNotEmpty())
                return@withContext authenticationSource.getUserPairsByUIds(contributors)

            return@withContext emptyMap()
        }
    }

    //----------------------------------------------------------------------------------------------------------------
    //END OF "OTHER USERS METHODS" BLOCK
    //----------------------------------------------------------------------------------------------------------------

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

    suspend fun saveNewTrip(trip: Trip, tripIdentifier: TripIdentifier) {

        withContext(Dispatchers.IO) {

            when(tripIdentifier.contributors.isEmpty()) {

                true -> {

                    uploadTripToLocalDatabase(
                        trip = trip
                    )

                    if(tripIdentifier.location.equals("remote")) {
                        deleteCurrentTripFromRemoteDatabase(trip)
                    }
                }
                false -> {

                    uploadTripToRemoteDatabase(
                        trip = trip,
                        tripIdentifier = tripIdentifier
                    )

                    if(tripIdentifier.location.equals("local")) {
                        deleteCurrentTripFromLocalDatabase(trip)
                    }
                }
            }
        }
    }

    suspend fun deleteCurrentTrip(trip: Trip, tripIdentifier: TripIdentifier) {

        CoroutineScope(coroutineContext).launch {

            when( tripIdentifier.location) {

                "local" -> { deleteCurrentTripFromLocalDatabase(trip) }
                "remote" -> { deleteCurrentTripFromRemoteDatabase(trip) }
            }
        }
    }

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

    suspend fun getCurrentTripData(tripIdentifier: TripIdentifier) {

        withContext(Dispatchers.IO) {

            when(tripIdentifier.location) {

                "local" ->  {
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
    }

    suspend fun resetCurrentTrip() {

        withContext(Dispatchers.IO) {

            val newTrip = Trip()

            _currentTripState.update {
                it.copy(
                    trip = newTrip,
                    tripIdentifier = TripIdentifier(newTrip.uUID)
                )
            }
        }
    }

    suspend fun setCurrentTrip(trip: Trip, tripIdentifier: TripIdentifier) {

        withContext(Dispatchers.IO) {

            _currentTripState.update {

                it.copy(

                    trip = trip,
                    tripIdentifier = tripIdentifier
                )
            }
        }
    }

    suspend fun initAddUpdateTrip(startPlace: Place, places: List<Place>) {

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

    suspend fun setCurrentTripContributors(contributors: Map<String, Boolean>) {

        withContext(Dispatchers.IO) {

            val usernames = async {
                getUsernamesByUIDs(
                    uIds = contributors.map { it.key }
                )
            }

            _currentTripState.update {

                it.copy(
                    tripIdentifier = it.tripIdentifier.copy(

                        contributors = contributors
                    )
                )
            }

            _currentTripState.update {

                it.copy(
                    tripIdentifier = it.tripIdentifier.copy(

                        contributorsUsernames = usernames.await()
                    )
                )
            }
        }
    }


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

    suspend fun getUsernamesByUIDs(uIds: List<String>): List<String> {

        val usernames = CoroutineScope(coroutineContext).async {

            authenticationSource.getUserPairsByUIds(
                uIds = uIds
            )
        }

        return usernames.await().map { it.value }

    }


    /**FirebaseDatabase methods block
     * -------------------------
     * Methods related to storing [Trip]s in the Firebase realtime database
     * [fetchMyTripsFromFirebase] fetches the trips uploaded by the currently logged-in user
     * [fetchContributedTripsFromFirebase] fetches the trips to which the current user is added as a contributor
     * */
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF FIREBASE DATABASE MANAGEMENT METHODS
    //----------------------------------------------------------------------------------------------------------------

    suspend fun uploadTripToRemoteDatabase(trip: Trip, tripIdentifier: TripIdentifier) {

        withContext(Dispatchers.IO) {

            Log.d("FirebaseDatabaseRepository", _currentTripState.value.trip.uUID.toString())

            firebaseRemoteDataSource.uploadTrip(
                trip = trip,
                firebaseIdentifier = tripIdentifier.copy(
                    creatorUID = firebaseUser!!.uid
                )
            )

            //resetCurrentTrip()
        }
    }

    suspend fun deleteCurrentTripFromRemoteDatabase(trip: Trip) {

        withContext(Dispatchers.IO) {

            firebaseRemoteDataSource.deleteTrip(
                uuid = trip.uUID
            )

            //resetCurrentTrip()
        }
    }

    suspend fun getCurrentRemoteTripData(tripIdentifier: TripIdentifier ) {

        withContext(Dispatchers.IO) {

            val currentTrip = async {
                firebaseRemoteDataSource.findTripById(
                    uuid = tripIdentifier.uuid.toString()
                )
            }

            Log.d("FirebaseDatabaseUID", tripIdentifier.uuid.toString())
            Log.d("FirebaseDatabaseTitle", tripIdentifier.title.toString())
            Log.d("FirebaseDatabaseLocation", tripIdentifier.location.toString())
            Log.d("FirebaseDatabaseCreator", tripIdentifier.creatorUID.toString())
            Log.d("FirebaseDatabaseContributorUID", tripIdentifier.contributors.map { it.key }.toString())

            /*updateCurrentTripData(
                trip = currentTrip.await(),
                tripIdentifier = tripIdentifier
            )*/

            _currentTripState.update {
                it.copy(
                    tripIdentifier = tripIdentifier
                )
            }

            _currentTripState.update {
                it.copy(
                    trip = currentTrip.await()
                )
            }

        }
    }

    /** [fetchMyTripsFromFirebase]
     *  fetches trips uploaded by the current user
     *  then updates the [_tripsStateFlow] with the received data
     * */
    suspend fun fetchMyTripsFromFirebase() {

        withContext(Dispatchers.IO) {

            if (firebaseUser != null) {
                val myTrips = firebaseRemoteDataSource.fetchMyTrips(uid = firebaseUser!!.uid)
                    .filterNotNull()

                val fullTrips = myTrips.map { trip ->
                    val usernames =
                        authenticationSource.getUserPairsByUIds(trip.contributors.keys.toList())
                            .map { it.value }

                    trip.copy(contributorsUsernames = usernames)
                }

                _tripsStateFlow.update { it.copy(trips = fullTrips) }
            }
        }
    }

    /** [fetchContributedTripsFromFirebase]
     *  fetches trips that has the current user's UID among the contributors
     *  then updates the [_tripsStateFlow] with the received data
     */
    suspend fun fetchContributedTripsFromFirebase() {

        withContext(Dispatchers.IO) {

            if (firebaseUser != null) {

                val contributedTrips = firebaseRemoteDataSource.fetchContributedTrips(
                    uid = firebaseUser!!.uid
                ).filterNotNull()

                val fullTrips = contributedTrips.map { trip ->
                    val usernames =
                        authenticationSource.getUserPairsByUIds(trip.contributors.keys.toList())
                            .map { it.value }

                    trip.copy(contributorsUsernames = usernames)
                }

                _tripsStateFlow.update { it.copy(trips = fullTrips) }
            }
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

    suspend fun deleteCurrentTripFromLocalDatabase(trip: Trip) {
        withContext(Dispatchers.IO) {

            roomLocalDataSource.deleteTrip(
                trip = trip
            )

            //resetCurrentTrip()
        }
    }

    suspend fun getCurrentLocalTripData( tripIdentifier: TripIdentifier) {

        withContext(Dispatchers.IO) {

            val currentTrip = async {
                roomLocalDataSource.findTripById(
                    uuid = tripIdentifier.uuid.toString()
                )
            }

            _currentTripState.update {
                it.copy(
                    tripIdentifier = tripIdentifier
                )
            }

            _currentTripState.update {
                it.copy(
                    trip = currentTrip.await()
                )
            }

        }

    }

    /** [fetchAllLocalSavedTrips]
     *  fetches all [Trip]s stored in the device's local storage
     *  then updates the [_tripsStateFlow] with the returned data
     */
    suspend fun fetchAllLocalSavedTrips() {

        withContext(Dispatchers.IO) {

            try {


                val trips = async { roomLocalDataSource.fetchTripIdentifiers() }

                _tripsStateFlow.update {

                    it.copy(
                        trips = trips.await()
                    )
                }

            } catch (e: Exception) {

                Log.e("FileReader", "Error reading file: saved_trips.json \n error:", e)
            }
        }
    }

    /** [uploadTripToLocalDatabase]
     *  writes the list of [Trip]s passed as the [tripToUpload] parameter of this function to the device's
     *  local storage
     */
    suspend fun uploadTripToLocalDatabase(trip: Trip){

        withContext(Dispatchers.IO){

            try {

                roomLocalDataSource.uploadTrip(
                    trip = trip
                )

                //resetCurrentTrip()
            }catch (e: Exception){

                Log.e("RoomLocalDatabase", "Error writing to local database \n error:", e)
            }
        }
    }
    //----------------------------------------------------------------------------------------------------------------
    //END OF LOCALLY SAVED TRIPS MANAGEMENT METHODS
    //----------------------------------------------------------------------------------------------------------------


    /** State holder data class definitions
     * -----------------------------------
     * Definitions of data classes that store information related to the data have to be shown on screen
     * [TripsState] class for holding the information of the [FragmentUser]'s UI
     * [User] class for holding the relevant information of the currently logged-in user
     * */

    data class TripsState(val trips: List<TripIdentifier> = emptyList())
    data class UserState(val username: String? = null)

    data class CurrentTrip(val trip: Trip = Trip(),
                           val tripIdentifier: TripIdentifier = TripIdentifier(uuid = trip.uUID)
    )

    @IgnoreExtraProperties
    data class TripIdentifier(
        @get:Exclude var uuid: String? = null,
        @get:Exclude var location: String? = null,
        var title: String? = null,
        @Ignore var contributors: Map<String, Boolean> = emptyMap(),
        @Ignore
        @get:Exclude
        @set:Exclude var contributorsUsernames: List<String> = emptyList(),
        var creatorUID: String? = null
    ): Serializable {

        constructor(uuid: String?, title: String?) : this() {
            this.uuid = uuid
            this.title = title
        }
    }
}
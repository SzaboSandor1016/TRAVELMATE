package com.example.core.remotedatasources.tripremotedatasource.data.datasource

import android.util.Log
import com.example.core.remotedatasources.tripremotedatasource.domain.datasource.FirebaseRemoteDataSource
import com.example.core.remotedatasources.tripremotedatasource.domain.models.TripIdentifierRemoteEntityModel
import com.example.core.remotedatasources.tripremotedatasource.domain.models.TripRemoteEntityModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.resume

/** [FirebaseRemoteDataSourceImpl]
 * implements the [com.example.travel_mate.data.FirebaseRemoteDataSource] interface
 * A class to manage every data related operations in [com.google.firebase.Firebase] database
 */
class FirebaseRemoteDataSourceImpl: FirebaseRemoteDataSource {

    companion object {

        const val SAVES_DATABASE_REFERENCE_STRING = "saves"
        const val SAVES_IDENTIFIERS_REFERENCE_STRING = "saves_identifiers"
        const val SAVES_CREATOR_UID_DATABASE_REFERENCE_STRING = "creatorUID"
        const val SAVES_CONTRIBUTOR_UID_DATABASE_REFERENCE_STRING = "contributorUIDs"
        const val SAVES_CONTRIBUTORS_DATABASE_REFERENCE_STRING = "contributors"
    }

    private val database: DatabaseReference = Firebase.database.reference

    /** [uploadTrip]
     * calls the [uploadIdentifierForTrip] function
     * and if it succeeds calls the [uploadTripData] function too
     */
    override suspend fun uploadTrip(trip: TripRemoteEntityModel, firebaseIdentifier: TripIdentifierRemoteEntityModel) {

        withContext(Dispatchers.IO) {

            val success = async {
                uploadIdentifierForTrip(
                    tripFirebaseIdentifier = firebaseIdentifier
                )
            }

            when (success.await()) {
                true -> {
                    uploadTripData(
                        trip = trip
                    )
                }

                false -> {
                    Log.e(
                        "FirebaseDatabase",
                        "creating trip data branch failed due to identifier creation error"
                    )
                }

            }
        }
    }
    /** [deleteTrip]
     * calls the [deleteTripIdentifierBranchByUUID] and the [deleteTripDataBranchByUUID] functions
     */
    override suspend fun deleteTrip(uuid: String) {

        CoroutineScope(coroutineContext).launch {

            deleteTripDataBranchByUUID(
                uuid = uuid
            )

            deleteTripIdentifierBranchByUUID(
                uuid = uuid
            )

        }

    }

    /** [deleteTripDataBranchByUUID]
     * deletes the data branch of a trip which [uuid] matches the [uuid] passed as parameter
     */
    suspend fun deleteTripDataBranchByUUID(uuid: String) {

        CoroutineScope(coroutineContext).launch {

            database.child(SAVES_DATABASE_REFERENCE_STRING)
                .child(uuid)
                .removeValue()
                .addOnSuccessListener { l ->
                    Log.d("FirebaseDatabase", " delete trip data branch: success")
                }
                .addOnFailureListener { l ->
                    Log.e("FirebaseDatabase", "delete trip data branch: error", l)
                }
        }
    }
    /** [deleteTripIdentifierBranchByUUID]
     * deletes the identifier branch of a trip which [uuid] matches the [uuid] passed as parameter
     */
    suspend fun deleteTripIdentifierBranchByUUID(uuid: String) {

        CoroutineScope(coroutineContext).launch {

            database.child(SAVES_IDENTIFIERS_REFERENCE_STRING)
                .child(uuid)
                .removeValue()
                .addOnSuccessListener { l ->
                    Log.d("FirebaseDatabase", " delete trip identifiers branch: success")
                }
                .addOnFailureListener { l ->
                    Log.e("FirebaseDatabase", "delete trip identifiers branch: error", l)
                }
        }
    }
    /** [findTripById]
     * gets the trip uploaded to the [com.google.firebase.ktx.Firebase] database based on its [uuid] passed as parameter
     */
    override suspend fun findTripById(uuid: String): TripRemoteEntityModel?
    = suspendCancellableCoroutine { continuation ->

        database.child(SAVES_DATABASE_REFERENCE_STRING)
            .orderByKey()
            .equalTo(uuid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val trip = snapshot.child(uuid).getValue(TripRemoteEntityModel::class.java)

                    if (trip != null) {
                        val actualTrip = trip.copy(
                            uUID = snapshot.child(uuid).key!!
                        )

                        Log.d("FirebaseDatabase", actualTrip.uUID!!)
                        Log.d("FirebaseDatabase", "find trip by id: success")
                        continuation.resume(actualTrip)
                    } else {
                        Log.e("FirebaseDatabase", "find trip by id: error( trip not found )",)
                        continuation.resume(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseDatabase", "find trip by id: error", error.toException())
                    continuation.resumeWithException(error.toException())
                }

            })
    }
    /** [uploadTripData]
     * creates a data branch for the trip currently being uploaded to the [com.google.firebase.ktx.Firebase] database
     */
    suspend fun uploadTripData(trip: TripRemoteEntityModel) {

        withContext(Dispatchers.IO) {
            database.child(SAVES_DATABASE_REFERENCE_STRING)
                .child(trip.uUID.toString())
                .setValue(trip)
                .addOnSuccessListener { l ->
                    Log.d("FirebaseDatabase", " create trip data branch: success")
                }
                .addOnFailureListener { l ->
                    Log.e("FirebaseDatabase", "create trip data branch: error", l)
                }
        }

    }
    /** [uploadIdentifierForTrip]
     * creates an identifier branch for the trip currently being uploaded to the [com.google.firebase.ktx.Firebase] database
     */
    suspend fun uploadIdentifierForTrip(tripFirebaseIdentifier: TripIdentifierRemoteEntityModel): Boolean
    = suspendCancellableCoroutine { continuation ->

        database.child(SAVES_IDENTIFIERS_REFERENCE_STRING)
            .child(tripFirebaseIdentifier.uuid.toString())
            .setValue(tripFirebaseIdentifier)
            .addOnSuccessListener { l ->
                continuation.resume(true)
                Log.d("FirebaseDatabase", " create identifier branch for trip: success")
            }
            .addOnFailureListener { l ->
                Log.e("FirebaseDatabase", "creating identifier branch for trip: error", l)
                continuation.resume(false)
            }
    }



    /** [fetchMyTrips]
     * gets all trips' [com.example.travel_mate.data.TripRepositoryImpl.TripIdentifier] that the currently signed in user has uploaded to the [com.google.firebase.ktx.Firebase] database
     */
    override suspend fun fetchMyTrips(uid: String): List<TripIdentifierRemoteEntityModel> =
        suspendCancellableCoroutine { continuation ->

            database.child(SAVES_IDENTIFIERS_REFERENCE_STRING)
                .orderByChild(SAVES_CREATOR_UID_DATABASE_REFERENCE_STRING)
                .equalTo(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val myTripIdentifiers = snapshot.children.map { dataSnapshot ->

                            val tripIdentifier =
                                dataSnapshot.getValue(TripIdentifierRemoteEntityModel::class.java)

                            tripIdentifier?.copy(
                                //location = "remote",
                                uuid = dataSnapshot.key.toString()
                            )?: TripIdentifierRemoteEntityModel()
                        }
                        continuation.resume(myTripIdentifiers)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseDatabase", "fetch my trips: error", error.toException())
                        continuation.resumeWithException(error.toException())
                    }

                })
        }
    /** [fetchContributedTrips]
     * gets all trips' [com.example.travel_mate.data.TripRepositoryImpl.TripIdentifier] that the currently signed in user has contributed to
     */
    override suspend fun fetchContributedTrips(uid: String): List<TripIdentifierRemoteEntityModel>
    = suspendCancellableCoroutine { continuation ->

        database.child(SAVES_IDENTIFIERS_REFERENCE_STRING)
            .orderByChild("$SAVES_CONTRIBUTOR_UID_DATABASE_REFERENCE_STRING/$uid")
            .equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    Log.d("contributedTrips", snapshot.key.toString())

                    val contributedTripIdentifiers = snapshot.children.map { dataSnapshot ->

                        Log.d("contributedTrips", dataSnapshot.key.toString())

                        val tripIdentifier =
                            dataSnapshot.getValue(TripIdentifierRemoteEntityModel::class.java)

                        tripIdentifier?.copy(
                            uuid = dataSnapshot.key.toString()
                        )?: TripIdentifierRemoteEntityModel()
                    }
                    continuation.resume(contributedTripIdentifiers)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseDatabase", "fetch contributed trips: error", error.toException())
                    continuation.resumeWithException(error.toException())
                }

            })
    }
    /** [deleteTripsByUserUid]
     * deletes all shared trips that has the uid of the currently signed in user's
     * uid as "creatorUid"
     */
    override suspend fun deleteTripsByUserUid(uid: String) {

        withContext(Dispatchers.IO) {

            val myTrips = fetchMyTrips(uid)

            val contributedTrips = fetchContributedTrips(uid)

            myTrips.forEach {

                deleteTrip(it.uuid.toString())
            }

            contributedTrips.forEach {

                deleteUidFromContributedTrips(
                    uid = uid,
                    tripUUID = it.uuid.toString()
                )
            }

        }

    }
    /** [deleteUidFromContributedTrips]
     * deletes the currently signed in user's uid from a shared trip's identifier branch
     */
    override suspend fun deleteUidFromContributedTrips(uid: String, tripUUID: String) {

        withContext(Dispatchers.IO) {

            database.child(SAVES_IDENTIFIERS_REFERENCE_STRING)
                .child("$tripUUID/$SAVES_CONTRIBUTORS_DATABASE_REFERENCE_STRING/$uid")
                .removeValue(object : DatabaseReference.CompletionListener {
                    override fun onComplete(
                        error: DatabaseError?,
                        ref: DatabaseReference
                    ) {
                        if (error != null) {
                            Log.e(
                                "FirebaseCurrentUser",
                                "delete user from other saves' contributors: error",
                                error.toException()
                            )
                        }
                    }
                })

            database.child(SAVES_IDENTIFIERS_REFERENCE_STRING)
                .child("$tripUUID/$SAVES_CONTRIBUTOR_UID_DATABASE_REFERENCE_STRING/$uid")
                .removeValue(object : DatabaseReference.CompletionListener {
                    override fun onComplete(
                        error: DatabaseError?,
                        ref: DatabaseReference
                    ) {
                        if (error != null) {
                            Log.e(
                                "FirebaseCurrentUser",
                                "delete user from other saves' contributors: error",
                                error.toException()
                            )
                        }
                    }
                })
        }

    }


}
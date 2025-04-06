package com.example.travel_mate

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.EventListener
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** [FirebaseAuthenticationSourceImpl]
 * implements the [FirebaseAuthenticationSource] interface
 * A class to manage every user related operations in [com.google.firebase.Firebase] database
 * and [FirebaseAuth]
 */
class FirebaseAuthenticationSourceImpl: FirebaseAuthenticationSource {

    companion object {

        const val USER_DATABASE_REFERENCE_STRING = "users"
        const val USERNAME_UID_PAIR_DATABASE_REFERENCE = "username_to_uid"
        const val USER_USERNAME_DATABASE_REFERENCE = "username"
        const val USER_CONTRIBUTORS_DATABASE_REFERENCE = "contributors"
    }

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    /** [checkUser]
     * checks if there is a user currently signed in
     */
    override suspend fun checkUser(): FirebaseUser?{

        return withContext(Dispatchers.IO) {

            val user = firebaseAuth.currentUser

            return@withContext user
        }
    }
    /** [createUser]
     * signs the user up with the email address of [email] and password of [password]
     * then calls the [createUserInDatabase] method
     */
    override suspend fun createUser(email: String, password: String, username: String): FirebaseUser?
    = suspendCancellableCoroutine {continuation ->
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    CoroutineScope(Dispatchers.IO).launch {
                        createUserInDatabase(
                            uid = firebaseAuth.currentUser!!.uid,
                            username = username
                        )
                    }

                    continuation.resume(firebaseAuth.currentUser)

                    Log.d("FirebaseAuth", "createUserWithEmail:success")
                } else {

                    continuation.resume(null)

                    Log.e("FirebaseAuth", "createUserWithEmail:error", task.exception)
                }
            }
    }
    /** [signOut]
     * sign the currently signed in user out
     * returns a null [FirebaseUser]
     */
    override fun signOut(): FirebaseUser? {

        firebaseAuth.signOut()

        return null
    }
    /** [signIn]
     * sign the user in with the email address of [email] and
     * password of [password]
     * then return the [FirebaseUser] of that user
     */
    override suspend fun signIn(email: String, password: String): FirebaseUser?
    = suspendCancellableCoroutine {continuation ->

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                Log.d("FirebaseAuth", "signInUserWithEmail:success")
                continuation.resume(firebaseAuth.currentUser)
            } else {

                Log.e("FirebaseAuth", "signInUserWithEmail:error", task.exception)
                continuation.resume(null)
            }
        }
    }
    /** [findUserByUsername]
     * returns the uid-username pair of a user whose username is passed as parameter
     */
    override suspend fun findUserByUsername(username: String): Pair<String, String>?
    = suspendCancellableCoroutine {continuation ->

            database.child(USERNAME_UID_PAIR_DATABASE_REFERENCE)
                .orderByValue()
                .equalTo(username)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val key = snapshot.children.firstOrNull()?.key
                        val value = snapshot.children.firstOrNull()?.value?.toString()

                        if (key != null && value != null) {
                            continuation.resume(Pair(key, value))
                            Log.d("FirebaseCurrentUser", "User found by uId: $key")
                        } else {
                            continuation.resume(null) // Return null if no data found
                            Log.d("FirebaseCurrentUser", "User not found by uId: returned null")
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseCurrentUser", "Error getting user-username pair", error.toException())
                        continuation.resumeWithException(error.toException())
                    }

                })

    }
    /** [getUserPairsByUsernames]
     * returns the uid-username pairs of users whose usernames are in the list passed as parameter
     */
    override suspend fun getUserPairsByUsernames(usernames: List<String>): Map<String, String> {

        return withContext(Dispatchers.IO) {

            val deferredList = usernames.map { username ->

                async { findUserByUsername(username) }
            }

            val results = deferredList.awaitAll()

            results.filterNotNull().toMap()
        }
    }
    /** [findUserByUidFromUsernameToUid]
     *  returns the uid-username pair of a user whose uid is passed as parameter
     */
    override suspend fun findUserByUidFromUsernameToUid(uid: String): Pair<String, String>? =
        suspendCancellableCoroutine { continuation ->

            database.child(USERNAME_UID_PAIR_DATABASE_REFERENCE)
                .orderByKey()
                .equalTo(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val key = snapshot.children.firstOrNull()?.key
                        val value = snapshot.children.firstOrNull()?.value?.toString()

                        if (key != null && value != null) {
                            continuation.resume(Pair(key, value))
                        } else {
                            continuation.resume(null) // Return null if no data found
                        }

                        Log.d("FirebaseCurrentUser", "User found by uId")
                    }

                    override fun onCancelled(error: DatabaseError) {

                        Log.e("FirebaseCurrentUser", "Error getting user-username pair", error.toException())
                        continuation.resumeWithException(error.toException())
                    }
                })
        }
    /** [getUserPairsByUIds]
     * returns the uid-username pairs of users whose uid is in the list passed as parameter
     */
    override suspend fun getUserPairsByUIds(uIds: List<String>): Map<String, String> {
        return coroutineScope {

            val deferredList = uIds.map { uid ->

                async { findUserByUidFromUsernameToUid(uid) } // Launch all Firebase calls in parallel
            }
            val results = deferredList.awaitAll() // Wait for all to finish

            results.filterNotNull().toMap() // Convert List<Pair<String, String>?> to Map<String, String>
        }
    }
    /** [setRecentContributors]
     * updates the currently signed in user's [uid] contributor list with the
     * uid-true pairs provided as parameter [users]
     */
    override suspend fun setRecentContributors(uid: String, users: Map<String, Boolean>) {

        withContext(Dispatchers.IO) {

            database.child(USER_DATABASE_REFERENCE_STRING)
                .child(uid)
                .child(USER_CONTRIBUTORS_DATABASE_REFERENCE)
                .setValue(users)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        Log.d("FirebaseCurrentUser", "add recent contributors in database: success")
                    } else {
                        Log.e("FirebaseCurrentUser", "add recent contributors in database: error", task.exception)
                    }
                }
        }
    }
    /** [getRecentContributorsOfUser]
     * Returns the UIDs of the recent contributors of the currently signed in user
     * accepts the uid of the current user as parameter
     */
    override suspend fun getRecentContributorsOfUser(uid: String): List<String>
    = suspendCancellableCoroutine { continuation ->

            database.child(USER_DATABASE_REFERENCE_STRING)
                .child(uid)
                .child(USER_CONTRIBUTORS_DATABASE_REFERENCE)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val contributors = parseContributorsDataSnapshot(snapshot)

                        continuation.resume(contributors)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseCurrentUser", "get recent contributors from database: error", error.toException())

                        continuation.resumeWithException(error.toException())
                    }

                })
    }
    /**
     *
     */
    private fun parseContributorsDataSnapshot(dataSnapshot: DataSnapshot): List<String> {

        return dataSnapshot.children.map { childSnapshot ->
            childSnapshot.key!!
        }
    }
    /** [createUserInDatabase]
     * creates a branch for the user in the [FirebaseDatabase] database
     * and create a uid-username pair too
     */
    suspend fun createUserInDatabase(uid: String, username: String) {

        withContext(Dispatchers.Default) {

            database.child("username_to_uid")
                .child(firebaseAuth.currentUser!!.uid)
                .setValue(username).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseCurrentUser", "create username-uid pair in database: success")
                    } else {
                        Log.e("FirebaseCurrentUser", "create username-uid pair in database: error", task.exception)
                    }
                }

            database.child(USER_DATABASE_REFERENCE_STRING)
                .child(uid)
                .child(USER_USERNAME_DATABASE_REFERENCE)
                .setValue(username).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseCurrentUser", "create user in database: success")
                    } else {
                        Log.e("FirebaseCurrentUser", "create user in database: error", task.exception)
                    }
                }
        }
    }
    /** [getUsernameByUID]
     * return the username of the user whose uid is the same as the one given in parameter
     */
    override suspend fun getUsernameByUID(uid: String): String?
    = suspendCancellableCoroutine { continuation ->

        database.child(USER_DATABASE_REFERENCE_STRING)
            .child(uid)
            .child(USER_USERNAME_DATABASE_REFERENCE)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    continuation.resume(snapshot.value.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }

            })

    }
    /** [resetPassword]
     *  send a password reset Email to the given e-mail address in parameter
     */
    override suspend fun resetPassword(email: String): Boolean
    = suspendCancellableCoroutine{ continuation ->

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseCurrentUser", "reset password: success")
                    continuation.resume(true)
                } else {
                    Log.e("FirebaseCurrentUser", "reset password: error", task.exception)
                    continuation.resume(false)
                }
            }
    }
    /** [changePassword]
     *  change the password of the currently signed in user
     */
    override suspend fun changePassword(currentPassword: String, newPassword: String): Boolean
    = suspendCancellableCoroutine { continuation ->

        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser != null) {
            val email = firebaseUser.email

            val credential = EmailAuthProvider.getCredential(email!!, currentPassword)

            firebaseUser.reauthenticate(credential)
                .addOnCompleteListener {task ->

                    if (task.isSuccessful) {
                        Log.d("FirebaseCurrentUser", "re-authenticate user: success")

                        firebaseUser.updatePassword(newPassword).addOnCompleteListener {task ->
                            if (task.isSuccessful) {
                                Log.d("FirebaseCurrentUser", "update user password: success")
                                continuation.resume(true)
                            } else {
                                Log.e("FirebaseCurrentUser", "update user password: error", task.exception)
                                continuation.resume(false)
                            }
                        }

                    } else {
                        Log.e("FirebaseCurrentUser", "re-authenticate user: error", task.exception)
                        continuation.resume(false)
                    }
                }
        }

    }
    /** [deleteUser]
     * deletes the current user's account from [FirebaseAuth]
     */
    override suspend fun deleteUser(password: String): Boolean
    = suspendCancellableCoroutine { continuation ->

        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser != null) {
            val email = firebaseUser.email

            val credential = EmailAuthProvider.getCredential(email!!, password)

            firebaseUser.reauthenticate(credential)
                .addOnCompleteListener {task ->

                    if (task.isSuccessful) {
                        Log.d("FirebaseCurrentUser", "re-authenticate user: success")

                        firebaseUser.delete().addOnCompleteListener {task ->
                            if (task.isSuccessful) {
                                Log.d("FirebaseCurrentUser", "delete user: success")
                                continuation.resume(true)
                            } else {
                                Log.e("FirebaseCurrentUser", "delete user: error", task.exception)
                                continuation.resume(false)
                            }
                        }

                    } else {
                        Log.e("FirebaseCurrentUser", "re-authenticate user: error", task.exception)
                        continuation.resume(false)
                    }
                }
        }
    }
    /** [deleteUserData]
     * delete the current user's data from the "users" branch of the [FirebaseDatabase] database
     */
    override suspend fun deleteUserData(uid: String): Boolean
    = suspendCancellableCoroutine { continuation ->

        database.child(USER_DATABASE_REFERENCE_STRING)
            .child(uid)
            .removeValue(object: DatabaseReference.CompletionListener {
                override fun onComplete(
                    error: DatabaseError?,
                    ref: DatabaseReference
                ) {
                    if (error != null) {
                        Log.e("FirebaseCurrentUser", "delete user username-to-uid pair: error", error.toException())
                        continuation.resume(false)
                    }
                    continuation.resume(true)
                }

            })
    }
    /** [deleteUserUsernameUIDPair]
     * delete the reference of the current user's uid-username pair
     */
    override suspend fun deleteUserUsernameUIDPair(uid: String): Boolean
            = suspendCancellableCoroutine { continuation ->

        database.child(USERNAME_UID_PAIR_DATABASE_REFERENCE)
            .child(uid)
            .removeValue(object: DatabaseReference.CompletionListener{
                override fun onComplete(
                    error: DatabaseError?,
                    ref: DatabaseReference
                ) {
                    if (error != null) {
                        Log.e("FirebaseCurrentUser", "delete user username-to-uid pair: error", error.toException())
                        continuation.resume(false)
                    }
                    continuation.resume(true)
                }
            })
    }
    /** [deleteUserUIDFromRecentContributors]
     * delete all the reference of the current user's [uid] among other users' contributors
     * calls the [getReferencesOfUIDFromContributors] to get the references
     */
    override suspend fun deleteUserUIDFromRecentContributors(uid: String) {

        withContext(Dispatchers.IO) {

            val databaseReferences = getReferencesOfUIDFromContributors(
                uid = uid
            )

            databaseReferences.forEach {

                it.child(USER_CONTRIBUTORS_DATABASE_REFERENCE)
                    .child(uid)
                    .removeValue(object : DatabaseReference.CompletionListener {
                        override fun onComplete(
                            error: DatabaseError?,
                            ref: DatabaseReference
                        ) {
                            if (error != null) {
                                Log.e("FirebaseCurrentUser", "delete user from contributors: error", error.toException())
                            }
                        }

                    })

            }
        }
    }
    /** [getReferencesOfUIDFromContributors]
     * get the reference of each instance the [uid] of the current user appears among other users contributors
     */
    suspend fun getReferencesOfUIDFromContributors(uid: String): List<DatabaseReference> =
        suspendCancellableCoroutine { continuation ->
            database.child(USER_DATABASE_REFERENCE_STRING)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val references = snapshot.children
                            .filter { it.child(USER_CONTRIBUTORS_DATABASE_REFERENCE).hasChild(uid) }
                            .map { it.ref }

                        continuation.resume(references)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseCurrentUser", "get reference of user as contributor: error")
                        continuation.resume(emptyList())
                    }
                })
        }

}
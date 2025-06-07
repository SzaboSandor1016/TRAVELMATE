package com.example.travel_mate.data

import android.util.Log
import com.example.travel_mate.domain.UserRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val firebaseAuthenticationSource: FirebaseAuthenticationSource
): UserRepository {

    private val _userState = MutableStateFlow(UserState())
    override val userState: StateFlow<UserState> = _userState.asStateFlow()

    private var firebaseUser: FirebaseUser? = null

    private val userCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO

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

    override fun getCurrentUserUid(): String {

        return _userState.value.user!!.uid
    }

    /** [checkUser]
     *  calls the [authenticationSource]'s [checkUser] function to find out
     *  if there is a user currently signed in
     *  then if there is, updates the [UserState] with the user's username
     *  and updates the value of the [firebaseUser] with the returned [FirebaseUser]
     */
    override suspend fun checkUser(): Flow<FirebaseUser?> {

        return withContext(userCoroutineDispatcher) {

            return@withContext flowOf(firebaseAuthenticationSource.checkUser())
        }
    }

    /** [createUser]
     * Calls the [createUser] function of the [authenticationSource]
     * then updates the [_tripsStateFlow] with the returned [FirebaseUser]
     */
    override suspend fun createUser(email: String, password: String, username: String): Flow<FirebaseUser?> {

        return withContext(userCoroutineDispatcher) {

            //todo check if user with username already exists if so notify the user;
            // maybe wait until sign up finishes then return to user fragment

            flowOf(
                firebaseAuthenticationSource.createUser(
                    email = email,
                    password = password,
                    username = username
                )
            )
        }
    }

    /** [signIn]
     *  Signs the user in to firebase at the [firebaseAuthenticationSource]'s [signIn] method
     *  with the given [email] and [password] parameters
     *  @return the found [FirebaseUser]
     */
    override suspend fun signIn(email: String, password: String): Flow<FirebaseUser?> {

        return withContext(userCoroutineDispatcher) {

            flowOf(
                firebaseAuthenticationSource.signIn(
                    email = email,
                    password = password
                )
            )
        }
    }

    /** [signOut]
     *  Signs the user out at the [authenticationSource]'s [signOut] method
     *  then updates the [_tripsStateFlow] with a new (default) [User]
     */
    override suspend fun signOut(): Flow<FirebaseUser?> {

        return withContext(userCoroutineDispatcher) {

            flowOf(firebaseAuthenticationSource.signOut())
        }
    }

    override suspend fun getUserUsernameByUid(uid: String): Flow<String> {

        return withContext(userCoroutineDispatcher) {

            val username = firebaseAuthenticationSource.getUsernameByUID(uid) ?: ""

            flowOf(username)
        }
    }


    //todo if the auth Token listener will be implemented this may not be necessary
    override suspend fun updateUserState(user: FirebaseUser?, username: String?) {

        withContext(userCoroutineDispatcher) {

            firebaseUser = user

            Log.d("FirebaseCurrentUser", username.toString())

            _userState.update {
                it.copy(
                    user = user,
                    username = username
                )
            }
        }
    }

    override suspend fun deleteCurrentUser(userUid: String, password: String) {

        withContext(userCoroutineDispatcher) {

            firebaseAuthenticationSource.removeFullUserData(
                uid = userUid,
                password = password
            )
            firebaseAuthenticationSource.deleteUser(
                password = password
            )

            checkUser()
        }
    }

    override suspend fun resetPassword(email: String) {

        withContext(userCoroutineDispatcher) {

            firebaseAuthenticationSource.resetPassword(
                email = email
            )
        }

    }

    override suspend fun changePassword(currentPassword: String, newPassword: String) {

        withContext(userCoroutineDispatcher) {

            firebaseAuthenticationSource.changePassword(
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

    override suspend fun findUserByUsername(username: String): Pair<String,String>? {

        return withContext(userCoroutineDispatcher) {

            return@withContext firebaseAuthenticationSource.findUserByUsername(
                username = username
            )
        }
    }


    override suspend fun selectUnselectContributor(uid: String) {

        withContext(userCoroutineDispatcher) {

            val contributors = _userState.value.contributors.toMutableMap()

            val toBeSelected = contributors[uid]

            if (toBeSelected != null) {

                contributors.replace(
                    uid,
                    toBeSelected.setSelected(toBeSelected.selected!!.not())
                )

                _userState.update {
                    it.copy(

                        contributors = contributors
                    )
                }
            }
        }
    }

    override suspend fun setRecentContributors(users: Map<String, Boolean>) {

        withContext(userCoroutineDispatcher) {

            firebaseAuthenticationSource.setRecentContributors(
                uid = firebaseUser!!.uid,
                users = users
            )
        }
    }

    //todo leave the request here and move the others to a UseState
    //todo reminder of the original function
    //override suspend fun getSelectableContributors() {
    //
    //        withContext(userCoroutineDispatcher) {
    //
    //            val contributorsOfTrip = _currentTripState.value.tripIdentifier?.contributors
    //
    //            val recentContributorsOfUser = getRecentContributorsOfUser().map {
    //
    //                Pair(
    //                    it.key,
    //                    Contributor(
    //                        uid = it.key,
    //                        username = it.value,
    //                        selected = false
    //                    )
    //                )
    //            }.toMap()
    //
    //            var fullContributors: MutableMap<String, Contributor> = mutableMapOf()
    //
    //            fullContributors.putAll(contributorsOfTrip.orEmpty())
    //
    //            fullContributors.putAll(recentContributorsOfUser.filter {
    //                !fullContributors.map { it.value.username.toString() }
    //                    .contains(it.value.username.toString())
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

    override suspend fun getRecentContributorsOfUser(uid: String): List<String> {

        return withContext(userCoroutineDispatcher) {

             firebaseAuthenticationSource.getRecentContributorsOfUser(uid)
        }
    }


    override suspend fun getUsersByUIDs(uIds: List<String>): Map<String, String> {

        return withContext(userCoroutineDispatcher) {

            firebaseAuthenticationSource.getUserPairsByUIds(
                uIds = uIds
            )
        }
    }

    override suspend fun getCurrentContributors(): Map<String, Contributor> = _userState.value.contributors


    override suspend fun setCurrentContributors(contributors: Map<String, Contributor>) {

        _userState.update {

            it.copy(

                contributors = contributors
            )
        }
    }


    /*override suspend fun getUsernamesByUIDs(uIds: List<String>): List<String> {

        return withContext(userCoroutineDispatcher) {

            firebaseAuthenticationSource.getUserPairsByUIds(
                uIds = uIds
            ).map { it.value }
        }
    }*/

    //----------------------------------------------------------------------------------------------------------------
    //END OF "OTHER USERS METHODS" BLOCK
    //----------------------------------------------------------------------------------------------------------------

    data class UserState(
        val user: FirebaseUser? = null,
        val username: String? = null,
        val contributors: Map<String, Contributor> = emptyMap()
    )
}
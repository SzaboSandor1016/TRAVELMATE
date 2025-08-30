package com.example.features.user.data.repositories

import com.example.core.auth.domain.FirebaseAuthenticationSource
import com.example.core.remotedatasources.tripremotedatasource.domain.datasource.FirebaseRemoteDataSource
import com.example.features.user.domain.models.UserUserDomainModel
import com.example.features.user.domain.repositories.UserRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class UserRepositoryImpl(
    private val appScope: CoroutineScope
): UserRepository {

    private val firebaseAuthenticationSource: FirebaseAuthenticationSource by inject(
        FirebaseAuthenticationSource::class.java)

    private val firebaseRemoteDataSource: FirebaseRemoteDataSource by inject(
        FirebaseRemoteDataSource::class.java
    )

    private val userCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO

    @OptIn(ExperimentalCoroutinesApi::class)
    private val userState = firebaseAuthenticationSource.userFlow().mapLatest {

        if (it == null) {

            UserUserDomainModel.SignedOut
        } else {

            val username = firebaseAuthenticationSource.getUsernameByUID(it.uid)

            //TODO refine the case if there is no username. It is a must though to have one so it is impossible
            UserUserDomainModel.SignedIn(
                userID = it.uid,
                username = username?: ""
            )
        }
    }.flowOn(Dispatchers.Default).stateIn(
        appScope,
        SharingStarted.Eagerly,
        UserUserDomainModel.SignedOut
    )

    /**FirebaseAuth methods block
     *-------------------------
     * Methods related to managing the firebase account of the current user on the local device
     * Check if there is a user currently signed in [getCurrentUserData]
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

    /*override fun getCurrentUserID(): String? {

        return when(_userState.value.user) {
            is UserUserDomainModel.SignedIn -> (_userState.value.user as UserUserDomainModel.SignedIn).userID
            is UserUserDomainModel.SignedOut -> null
        }
    }

    override suspend fun getCurrentContributors(): Map<String, ContributorUserDomainModel> {

        return when(_userState.value.user) {
            is UserUserDomainModel.SignedIn -> (_userState.value.user as UserUserDomainModel.SignedIn).contributors
            is UserUserDomainModel.SignedOut -> emptyMap()
        }
    }*/

    override fun getCurrentUserData(): Flow<UserUserDomainModel> {

        return userState.map { it }
    }



    /** [getCurrentUserData]
     *  calls the [authenticationSource]'s [getCurrentUserData] function to find out
     *  if there is a user currently signed in
     *  then if there is, updates the [UserState] with the user's username
     *  and updates the value of the [firebaseUser] with the returned [FirebaseUser]
     */
    /*override suspend fun checkUser(): FirebaseUser? {

        return withContext(userCoroutineDispatcher) {
            firebaseAuthenticationSource.checkUser()
        }
    }

    override suspend fun setUser(user: UserUserDomainModel) {

        withContext(userCoroutineDispatcher) {

            _userState.update {
                it.copy(
                    user = user
                )
            }
        }
    }*/

    /** [createUser]
     * Calls the [createUser] function of the [authenticationSource]
     * then updates the [_tripsStateFlow] with the returned [FirebaseUser]
     */
    override suspend fun createUser(email: String, password: String, username: String) {

        withContext(userCoroutineDispatcher) {

            firebaseAuthenticationSource.createUser(
                email = email,
                password = password,
                username = username
            )

            /*//todo check if user with username already exists if so notify the user;
            // maybe wait until sign up finishes then return to user fragment

            flowOf(

            )*/
        }
    }

    /** [signIn]
     *  Signs the user in to firebase at the [firebaseAuthenticationSource]'s [signIn] method
     *  with the given [email] and [password] parameters
     *  @return the found [FirebaseUser]
     */
    override suspend fun signIn(email: String, password: String) {

        withContext(userCoroutineDispatcher) {

            firebaseAuthenticationSource.signIn(
                email = email,
                password = password
            )
        }
    }

    /** [signOut]
     *  Signs the user out at the [authenticationSource]'s [signOut] method
     *  then updates the [_tripsStateFlow] with a new (default) [User]
     */
    override suspend fun signOut() {

        withContext(userCoroutineDispatcher) {

            firebaseAuthenticationSource.signOut()
        }
    }

    /*override suspend fun getUserUsernameByUid(uid: String): String? {

        return withContext(userCoroutineDispatcher) {

            firebaseAuthenticationSource.getUsernameByUID(uid)
        }
    }*/


    /*//todo if the auth Token listener will be implemented this may not be necessary
    override suspend fun updateUserState(user: FirebaseUser?, username: String?) {

        withContext(userCoroutineDispatcher) {

            Log.d("FirebaseCurrentUser", username.toString())

            _userState.update {
                it.copy(
                    user = user,
                    username = username
                )
            }
        }
    }*/

    override suspend fun deleteCurrentUser(password: String) {

        withContext(userCoroutineDispatcher) {

            val user = userState.value

            if (user is UserUserDomainModel.SignedIn) {

                firebaseAuthenticationSource.removeFullUserData(
                    uid = user.userID
                )
                firebaseAuthenticationSource.deleteUser(
                    password = password
                )

                firebaseRemoteDataSource.deleteTripsByUserUid(user.userID)
            }
            //getCurrentUserData()
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

            //checkUser()
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

    /*override suspend fun findUserByUsername(username: String): Pair<String,String>? {

        return withContext(userCoroutineDispatcher) {

            return@withContext firebaseAuthenticationSource.findUserByUsername(
                username = username
            )
        }
    }


    override suspend fun selectUnselectContributor(uid: String) {

        withContext(userCoroutineDispatcher) {

            if (_userState.value.user is UserUserDomainModel.SignedIn) {

                val contributors = (_userState.value.user as UserUserDomainModel.SignedIn).contributors.toMutableMap()

                val toBeSelected = contributors[uid]

                if (toBeSelected != null) {

                    contributors.replace(
                        uid,
                        toBeSelected.setSelected(toBeSelected.selected!!.not())
                    )

                    _userState.update {
                        it.copy(
                            user = (_userState.value.user as UserUserDomainModel.SignedIn).copy(
                                contributors = contributors
                            )
                        )
                    }
                }
            }
        }
    }*/

    /*override suspend fun setRecentContributors(users: Map<String, Boolean>) {

        withContext(userCoroutineDispatcher) {

            if (_userState.value.user is UserUserDomainModel.SignedIn)
                firebaseAuthenticationSource.setRecentContributors(
                uid = (_userState.value.user as UserUserDomainModel.SignedIn).userID,
                users = users
                )
        }
    }*/

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

    /*override suspend fun getRecentContributorsOfUser(uid: String): List<String> {

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

    override suspend fun setCurrentContributors(contributors: Map<String, ContributorUserDomainModel>) {

        if (_userState.value.user is UserUserDomainModel.SignedIn)
            _userState.update {

                it.copy(
                    user = (_userState.value.user as UserUserDomainModel.SignedIn).copy(
                        contributors = contributors
                    )
                )
            }
    }*/


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
}
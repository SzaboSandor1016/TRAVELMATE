package com.example.travel_mate

import com.google.firebase.auth.FirebaseUser

/** [FirebaseAuthenticationSource]
 * interface for the [FirebaseAuthenticationSourceImpl] class
 */
interface FirebaseAuthenticationSource {

    suspend fun checkUser(): FirebaseUser?

    suspend fun createUser(email: String, password: String, username: String): FirebaseUser?

    fun signOut(): FirebaseUser?

    suspend fun signIn(email: String, password: String): FirebaseUser?

    suspend fun getUsernameByUID(uid: String): String?

    suspend fun findUserByUsername(username: String): Pair<String, String>?

    suspend fun getUserPairsByUsernames(usernames: List<String>): Map<String, String>

    suspend fun findUserByUidFromUsernameToUid(uid: String): Pair<String, String>?

    suspend fun getUserPairsByUIds(uIds: List<String>): Map<String,String>

    suspend fun setRecentContributors(uid: String, users: Map<String, Boolean>)

    suspend fun getRecentContributorsOfUser(uid: String): List<String>

    suspend fun resetPassword(email: String): Boolean

    suspend fun changePassword(currentPassword: String, newPassword: String): Boolean

    suspend fun deleteUser(password: String): Boolean

    suspend fun deleteUserData(uid: String): Boolean

    suspend fun deleteUserUsernameUIDPair(uid: String): Boolean

    suspend fun deleteUserUIDFromRecentContributors(uid: String)
}
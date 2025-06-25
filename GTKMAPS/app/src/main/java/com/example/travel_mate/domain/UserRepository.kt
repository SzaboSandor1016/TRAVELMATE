package com.example.travel_mate.domain

import com.example.travel_mate.data.Contributor
import com.example.travel_mate.data.UserRepositoryImpl.UserState
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {

    val userState: StateFlow<UserState>

    fun getCurrentUserUid(): String?

    suspend fun checkUser(): Flow<FirebaseUser?>

    suspend fun createUser(email: String, password: String, username: String): Flow<FirebaseUser?>

    suspend fun signIn(email: String, password: String): Flow<FirebaseUser?>

    suspend fun signOut(): Flow<FirebaseUser?>

    suspend fun getUserUsernameByUid(uid: String): Flow<String>

    suspend fun updateUserState(user: FirebaseUser?, username: String?)

    suspend fun deleteCurrentUser(userUid: String, password: String)

    suspend fun resetPassword(email: String)

    suspend fun changePassword(currentPassword: String, newPassword: String)

    suspend fun findUserByUsername(username: String): Pair<String,String>?

    suspend fun selectUnselectContributor(uid: String)

    suspend fun setRecentContributors(users: Map<String, Boolean>)

    suspend fun getRecentContributorsOfUser(uid: String): List<String>

    suspend fun getUsersByUIDs(uIds: List<String>): Map<String, String>

    suspend fun getCurrentContributors(): Map<String, Contributor>

    suspend fun setCurrentContributors(contributors: Map<String, Contributor>)
}
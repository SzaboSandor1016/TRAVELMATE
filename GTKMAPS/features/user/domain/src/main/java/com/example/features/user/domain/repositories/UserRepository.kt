package com.example.features.user.domain.repositories

import com.example.features.user.domain.models.UserUserDomainModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    //val userState: Flow<UserUserDomainModel>

    //fun getCurrentUserID(): String?

    //suspend fun getCurrentContributors(): Map<String, ContributorUserDomainModel>

    fun getCurrentUserData(): Flow<UserUserDomainModel>

    //suspend fun checkUser(): FirebaseUser?

    //suspend fun setUser(user: UserUserDomainModel)

    suspend fun createUser(email: String, password: String, username: String)

    suspend fun signIn(email: String, password: String)

    suspend fun signOut()

    //suspend fun getUserUsernameByUid(uid: String): String?

    //suspend fun updateUserState(user: FirebaseUser?, username: String?)

    suspend fun deleteCurrentUser(password: String)

    suspend fun resetPassword(email: String)

    suspend fun changePassword(currentPassword: String, newPassword: String)

    //suspend fun findUserByUsername(username: String): Pair<String,String>?

    //suspend fun selectUnselectContributor(uid: String)

    //suspend fun setRecentContributors(users: Map<String, Boolean>)

    //suspend fun getRecentContributorsOfUser(uid: String): List<String>

    //suspend fun getUsersByUIDs(uIds: List<String>): Map<String, String>

    //suspend fun setCurrentContributors(contributors: Map<String, ContributorUserDomainModel>)
}
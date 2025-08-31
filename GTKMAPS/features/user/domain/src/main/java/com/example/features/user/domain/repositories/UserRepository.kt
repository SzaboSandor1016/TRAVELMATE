package com.example.features.user.domain.repositories

import com.example.features.user.domain.models.UserUserDomainModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getCurrentUserData(): Flow<UserUserDomainModel>

    suspend fun createUser(email: String, password: String, username: String)

    suspend fun signIn(email: String, password: String)

    suspend fun signOut()

    suspend fun deleteCurrentUser(password: String)

    suspend fun resetPassword(email: String)

    suspend fun changePassword(currentPassword: String, newPassword: String)
}
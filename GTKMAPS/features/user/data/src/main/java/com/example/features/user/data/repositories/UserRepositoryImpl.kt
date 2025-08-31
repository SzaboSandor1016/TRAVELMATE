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

    override fun getCurrentUserData(): Flow<UserUserDomainModel> {

        return userState.map { it }
    }

    override suspend fun createUser(email: String, password: String, username: String) {

        withContext(userCoroutineDispatcher) {

            firebaseAuthenticationSource.createUser(
                email = email,
                password = password,
                username = username
            )
        }
    }

    override suspend fun signIn(email: String, password: String) {

        withContext(userCoroutineDispatcher) {

            firebaseAuthenticationSource.signIn(
                email = email,
                password = password
            )
        }
    }

    override suspend fun signOut() {

        withContext(userCoroutineDispatcher) {

            firebaseAuthenticationSource.signOut()
        }
    }

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
        }
    }
}
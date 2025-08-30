package com.example.features.user.domain.usecases

import com.example.features.user.domain.models.UserUserDomainModel
import com.example.features.user.domain.repositories.UserRepository

class SignUpUserUseCase(
    private val userRepository: UserRepository,
    //private val updateUserUseCase: UpdateUserUseCase
) {
    suspend operator fun invoke(email: String, username: String, password: String) {

        userRepository.createUser(
            email = email,
            username = username,
            password = password
        )/*.collect { user ->

            val createdUser = if (user != null) {
                    UserUserDomainModel.SignedIn(
                        userID = user.uid,
                        username = username,
                        contributors = emptyMap()
                    )
            } else {
                UserUserDomainModel.SignedOut
            }

            updateUserUseCase(createdUser)
        }*/
    }
}
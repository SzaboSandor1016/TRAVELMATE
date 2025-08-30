package com.example.features.user.domain.usecases

import com.example.features.user.domain.models.UserUserDomainModel
import com.example.features.user.domain.repositories.UserRepository

class SignOutUserUseCase(
    private val userRepository: UserRepository,
    //private val updateUserUseCase: UpdateUserUseCase
) {

    suspend operator fun invoke() {

        userRepository.signOut()/*.collect {user ->

            updateUserUseCase(UserUserDomainModel.SignedOut)
        }*/
    }
}
package com.example.features.user.domain.usecases

import com.example.features.user.domain.models.UserUserDomainModel
import com.example.features.user.domain.repositories.UserRepository

class DeleteUserUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(password: String) {

        //

        userRepository.deleteCurrentUser(
            password = password
        )

        //updateUserUseCase(UserUserDomainModel.SignedOut)
    }
}
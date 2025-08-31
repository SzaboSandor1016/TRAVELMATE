package com.example.features.user.domain.usecases

import com.example.features.user.domain.repositories.UserRepository

class SignUpUserUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(email: String, username: String, password: String) {

        userRepository.createUser(
            email = email,
            username = username,
            password = password
        )
    }
}
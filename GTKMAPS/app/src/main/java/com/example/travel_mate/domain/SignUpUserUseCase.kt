package com.example.travel_mate.domain

class SignUpUserUseCase(
    private val userRepository: UserRepository,
    private val updateUserUseCase: UpdateUserUseCase
) {
    suspend operator fun invoke(email: String, username: String, password: String) {

        userRepository.createUser(
            email = email,
            username = username,
            password = password
        ).collect { user ->

            updateUserUseCase(user)
        }
    }
}
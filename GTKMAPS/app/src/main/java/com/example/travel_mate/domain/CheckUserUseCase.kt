package com.example.travel_mate.domain

class CheckUserUseCase(
    private val userRepository: UserRepository,
    private val updateUserUseCase: UpdateUserUseCase
) {

    suspend operator fun invoke() {

        userRepository.checkUser().collect { user ->

            updateUserUseCase(user)
        }
    }
}
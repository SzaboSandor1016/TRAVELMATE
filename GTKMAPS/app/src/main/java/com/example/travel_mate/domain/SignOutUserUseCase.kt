package com.example.travel_mate.domain

class SignOutUserUseCase(
    private val userRepository: UserRepository,
    private val updateUserUseCase: UpdateUserUseCase
) {

    suspend operator fun invoke() {

        userRepository.signOut().collect {user ->

            updateUserUseCase(user)
        }
    }
}
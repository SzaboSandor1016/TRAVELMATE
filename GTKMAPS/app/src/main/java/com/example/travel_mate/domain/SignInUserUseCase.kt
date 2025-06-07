package com.example.travel_mate.domain

class SignInUserUseCase(
    private val userRepository: UserRepository,
    private val updateUserUseCase: UpdateUserUseCase
){

    suspend operator fun invoke(email: String, password: String) {

        userRepository.signIn(
            email = email,
            password = password
        ). collect {user ->

            updateUserUseCase(user)
        }
    }
}
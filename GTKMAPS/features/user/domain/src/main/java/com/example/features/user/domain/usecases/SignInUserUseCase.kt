package com.example.features.user.domain.usecases

import com.example.features.user.domain.repositories.UserRepository

class SignInUserUseCase(
    private val userRepository: UserRepository,
    //private val checkUserUseCase: CheckUserUseCase
){

    suspend operator fun invoke(email: String, password: String) {

        userRepository.signIn(
            email = email,
            password = password
        )/*. collect {user ->

            checkUserUseCase()
        }
    }*/
    }
}
package com.example.travel_mate.domain

import com.google.firebase.auth.FirebaseUser

//todo if the auth Token listener will be implemented this may not be necessary
class UpdateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(firebaseUser: FirebaseUser?) {

        if (firebaseUser != null) {

            userRepository.getUserUsernameByUid(firebaseUser.uid).collect { username ->

                userRepository.updateUserState(firebaseUser, username)
            }
        } else {

            userRepository.updateUserState(
                null, null
            )
        }
    }
}
package com.example.features.user.presentation.models

sealed interface UserUserPresentationModel {

    data object SignedOut: UserUserPresentationModel {
    }

    data class SignedIn(
        val userID: String,
        val username: String,
    ): UserUserPresentationModel
}
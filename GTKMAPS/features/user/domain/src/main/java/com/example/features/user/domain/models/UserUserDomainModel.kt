package com.example.features.user.domain.models

sealed interface UserUserDomainModel {

    data object SignedOut: UserUserDomainModel {
    }

    data class SignedIn(
        val userID: String,
        val username: String
    ): UserUserDomainModel
}
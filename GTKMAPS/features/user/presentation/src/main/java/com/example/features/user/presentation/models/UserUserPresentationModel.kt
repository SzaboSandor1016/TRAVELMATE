package com.example.features.user.presentation.models

import com.example.features.user.domain.models.ContributorUserDomainModel

sealed interface UserUserPresentationModel {

    data object SignedOut: UserUserPresentationModel {
    }

    data class SignedIn(
        val userID: String,
        val username: String,
    ): UserUserPresentationModel
}
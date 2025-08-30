package com.example.features.user.presentation.models

data class UserStateUserPresentationModel(
        val user: UserUserPresentationModel = UserUserPresentationModel.SignedOut
    )
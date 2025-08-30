package com.example.features.user.domain.models

data class UserStateUserDomainModel(
        val user: UserUserDomainModel = UserUserDomainModel.SignedOut
    )
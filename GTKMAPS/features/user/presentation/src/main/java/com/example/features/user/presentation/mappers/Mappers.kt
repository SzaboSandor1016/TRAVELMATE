package com.example.features.user.presentation.mappers

import com.example.features.user.domain.models.UserUserDomainModel
import com.example.features.user.presentation.models.UserUserPresentationModel

fun UserUserDomainModel.toUserPresentationModel(): UserUserPresentationModel {

    return when(this){
        is UserUserDomainModel.SignedOut -> UserUserPresentationModel.SignedOut
        is UserUserDomainModel.SignedIn -> UserUserPresentationModel.SignedIn(
            userID = this.userID,
            username = this.username
        )
    }
}
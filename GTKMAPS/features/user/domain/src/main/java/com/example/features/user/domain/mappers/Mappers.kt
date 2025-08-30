package com.example.features.user.domain.mappers

import com.example.features.user.domain.models.UserStateUserDomainModel
import com.example.features.user.domain.models.UserUserDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

fun Flow<UserStateUserDomainModel>.toFlowOfUserData(): Flow<UserUserDomainModel> {

    return this.map {
        it.user
    }
}
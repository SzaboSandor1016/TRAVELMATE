package com.example.features.user.domain.usecases

import com.example.features.user.domain.models.UserUserDomainModel
import com.example.features.user.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUserDataUseCase(
    private val userRepository: UserRepository
) {

    operator fun invoke(): Flow<UserUserDomainModel> {
        return userRepository.getCurrentUserData()
    }
}
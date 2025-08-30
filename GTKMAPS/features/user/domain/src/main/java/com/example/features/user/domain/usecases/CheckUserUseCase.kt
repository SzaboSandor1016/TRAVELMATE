package com.example.features.user.domain.usecases

import com.example.features.user.domain.models.ContributorUserDomainModel
import com.example.features.user.domain.models.UserUserDomainModel
import com.example.features.user.domain.repositories.UserRepository

/*
class CheckUserUseCase(
    private val userRepository: UserRepository,
    private val updateUserUseCase: UpdateUserUseCase
) {

    suspend operator fun invoke() {

        val user = userRepository.checkUser()

        if (user != null) {
            val username = userRepository.getUserUsernameByUid(user.uid)

            if (username != null) {

                val recentContributorsOfUser = userRepository.getRecentContributorsOfUser(user.uid)

                val userPairs = userRepository.getUsersByUIDs(
                    uIds = recentContributorsOfUser
                ).map {

                    Pair(
                        it.key,
                        ContributorUserDomainModel(
                            uid = it.key,
                            username = it.value,
                            selected = false
                        )
                    )
                }.toMap()

                updateUserUseCase(
                    UserUserDomainModel.SignedIn(
                        userID = user.uid,
                        username = username,
                        contributors = userPairs
                    )
                )
                return
            }
        }

        updateUserUseCase(UserUserDomainModel.SignedOut)
    }
}*/

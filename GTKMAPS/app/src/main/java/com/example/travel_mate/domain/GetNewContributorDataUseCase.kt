package com.example.travel_mate.domain

import com.example.travel_mate.data.Contributor
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlin.collections.plus
import kotlin.collections.toMap

class GetNewContributorDataUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(username: String) {

        //todo notify user if username not found

        val user = userRepository.findUserByUsername(
            username = username
        ) ?: return

        val contributors = userRepository.getCurrentContributors()


        userRepository.setCurrentContributors(
            contributors = contributors.plus(

                Pair(
                    user.first,
                    Contributor(
                        uid = user.first.toString(),
                        username = user.second.toString(),
                        selected = true
                    )
                )
            )
        )
    }
}
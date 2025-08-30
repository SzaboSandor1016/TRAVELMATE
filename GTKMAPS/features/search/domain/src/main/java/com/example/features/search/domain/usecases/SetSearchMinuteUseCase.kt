package com.example.features.search.domain.usecases

import com.example.features.search.domain.repositories.SearchOptionsRepository

class SetSearchMinuteUseCase(
    private val searchOptionsRepository: SearchOptionsRepository
) {

    suspend operator fun invoke(index: Int) {

        searchOptionsRepository.setMinute(
            index = index
        )
    }
}
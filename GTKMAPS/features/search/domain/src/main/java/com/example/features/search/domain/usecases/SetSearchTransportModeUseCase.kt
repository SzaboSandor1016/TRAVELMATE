package com.example.features.search.domain.usecases

import com.example.features.search.domain.repositories.SearchOptionsRepository

class SetSearchTransportModeUseCase(
    private val searchOptionsRepository: SearchOptionsRepository
) {

    suspend operator fun invoke(index: Int) {

        searchOptionsRepository.setSearchTransportMode(
            index = index
        )
    }
}
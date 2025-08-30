package com.example.features.search.domain.usecases

import com.example.features.search.domain.repositories.SearchOptionsRepository

class ResetSearchOptionsUseCase(
    private val searchOptionsRepository: SearchOptionsRepository
) {

    suspend operator fun invoke() {

        searchOptionsRepository.resetSearchOptions()
    }
}
package com.example.features.search.domain.usecases

import com.example.features.search.domain.repositories.SearchRepository

class ResetSearchDetailsUseCase(
    private val searchRepository: SearchRepository
) {

    suspend operator fun invoke(allDetails: Boolean) {

        searchRepository.resetSearchDetails(
            all = allDetails
        )
    }
}
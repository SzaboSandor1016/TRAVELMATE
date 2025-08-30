package com.example.features.search.domain.usecases

import com.example.features.search.domain.repositories.SearchRepository

class RemovePlacesByCategoryUseCase(
    private val searchRepository: SearchRepository
) {

    suspend operator fun invoke(category: String) {

        searchRepository.removePlacesByCategory(
            category = category
        )
    }
}
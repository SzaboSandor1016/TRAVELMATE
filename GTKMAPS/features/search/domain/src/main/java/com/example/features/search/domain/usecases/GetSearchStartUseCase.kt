package com.example.features.search.domain.usecases

import com.example.features.search.domain.models.searchmodels.SearchStartInfoSearchDomainModel
import com.example.features.search.domain.repositories.SearchRepository
import kotlinx.coroutines.flow.Flow

class GetSearchStartUseCase(
    private val searchRepository: SearchRepository
) {

    operator fun invoke(): Flow<SearchStartInfoSearchDomainModel> {

        return searchRepository.getSearchStartInfo()
    }
}
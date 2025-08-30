package com.example.features.search.domain.usecases

import com.example.features.search.domain.models.searchoptionsmodels.SearchOptionsInfoSearchDomainModel
import com.example.features.search.domain.repositories.SearchOptionsRepository
import kotlinx.coroutines.flow.Flow

class GetSearchOptionsUseCase(
    private val searchOptionsRepository: SearchOptionsRepository
) {
    operator fun invoke(): Flow<SearchOptionsInfoSearchDomainModel> {
        return searchOptionsRepository.getSearchOptionsInfo()
    }
}
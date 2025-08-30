package com.example.features.search.domain.usecases

import com.example.features.search.domain.models.searchmodels.SearchDataSearchDomainModel
import com.example.features.search.domain.repositories.SearchRepository
import kotlinx.coroutines.flow.Flow

class GetSearchPlacesDataUseCase(
    private val searchRepository: SearchRepository
) {
    operator fun invoke(): Flow<SearchDataSearchDomainModel> {
        return searchRepository.getSearchPlacesData()
    }
}
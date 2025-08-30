package com.example.features.search.domain.usecases

import com.example.features.search.domain.models.searchmodels.PlaceDataSearchDomainModel
import com.example.features.search.domain.repositories.SearchRepository
import kotlinx.coroutines.flow.Flow

class GetSearchStartDataUseCase(
    private val searchRepository: SearchRepository
) {
    operator fun invoke(): Flow<PlaceDataSearchDomainModel?> {
        return searchRepository.getSearchStartData()
    }
}
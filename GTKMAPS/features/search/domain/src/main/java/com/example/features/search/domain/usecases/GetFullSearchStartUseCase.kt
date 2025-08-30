package com.example.features.search.domain.usecases

import com.example.features.search.domain.models.searchmodels.PlaceSearchDomainModel
import com.example.features.search.domain.repositories.SearchRepository

class GetFullSearchStartUseCase(
    private val searchRepository: SearchRepository
) {

    operator fun invoke(): PlaceSearchDomainModel? {

        return searchRepository.getStartPlace()
    }
}
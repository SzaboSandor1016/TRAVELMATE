package com.example.features.search.domain.usecases

import com.example.features.search.domain.models.searchmodels.PlaceSearchDomainModel
import com.example.features.search.domain.repositories.SearchRepository

class InitSearchUseCase(
    private val searchRepository: SearchRepository,
) {

    suspend operator fun invoke(startPlace: PlaceSearchDomainModel) {

        searchRepository.initNewSearch(
            startPlace = startPlace
        )
    }
}
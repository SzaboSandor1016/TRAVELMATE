package com.example.features.search.domain.usecases

import com.example.features.search.domain.models.searchmodels.PlaceSearchDomainModel

class InitSearchWithSelectedStartUseCase(
    private val initSearchUseCase: InitSearchUseCase
) {

    suspend operator fun invoke(startPlace: PlaceSearchDomainModel) {

        initSearchUseCase(
            startPlace = startPlace
        )
    }
}
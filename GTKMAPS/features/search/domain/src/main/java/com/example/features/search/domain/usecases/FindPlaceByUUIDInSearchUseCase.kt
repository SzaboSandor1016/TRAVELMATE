package com.example.features.search.domain.usecases

import com.example.features.search.domain.models.searchmodels.PlaceSearchDomainModel
import com.example.features.search.domain.repositories.SearchRepository


class FindPlaceByUUIDInSearchUseCase(
    private val searchRepository: SearchRepository
) {

    operator fun invoke(placeUUID: String): PlaceSearchDomainModel? {

        return searchRepository.getPlaceByUUID(
            placeUUID = placeUUID
        )
    }
}
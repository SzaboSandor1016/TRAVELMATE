package com.example.features.search.domain.usecases

import com.example.features.search.domain.models.searchmodels.PlaceSearchDomainModel
import com.example.features.search.domain.repositories.SearchRepository


class GetSelectedPlaceDataUseCase(
    private val searchRepository: SearchRepository
) {

    suspend operator fun invoke(placeUUID: String): PlaceSearchDomainModel? {

        return searchRepository.getCurrentPlaceByUUID(uuid = placeUUID)
    }
}

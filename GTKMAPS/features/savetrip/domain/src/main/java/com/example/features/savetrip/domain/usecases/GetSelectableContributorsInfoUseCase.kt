package com.example.features.savetrip.domain.usecases

import com.example.features.savetrip.domain.models.ContributorSaveTripDomainModel
import com.example.features.savetrip.domain.models.ContributorsInfoSaveTripDomainModel
import com.example.features.savetrip.domain.repository.SaveTripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetSelectableContributorsInfoUseCase(
    private val saveTripRepository: SaveTripRepository
) {

    operator fun invoke(): Flow<List<ContributorSaveTripDomainModel>> {

        return saveTripRepository.getSelectableContributorsInfo()
    }
}
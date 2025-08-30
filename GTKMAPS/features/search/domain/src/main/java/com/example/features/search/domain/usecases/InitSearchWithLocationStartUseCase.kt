package com.example.features.search.domain.usecases

import com.example.core.remotedatasources.reversegeocode.domain.repository.ReverseGeoCodeRepository
import com.example.app.location.domain.usecases.GetCurrentLocationUseCase
import com.example.features.search.domain.mappers.mapToSearchStartPlace

class InitSearchWithLocationStartUseCase(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val reverseGeoCodeRepository: ReverseGeoCodeRepository,
    private val initSearchUseCase: InitSearchUseCase
) {

    suspend operator fun invoke() {

        getCurrentLocationUseCase().collect{ location ->


            reverseGeoCodeRepository.getReverseGeoCode(
                latitude = location?.latitude?: 0.0,
                longitude = location?.longitude?: 0.0
            ).collect{ reverseGeoCode ->

                val reverseGeoCodeResults = reverseGeoCode.mapToSearchStartPlace()

                if (reverseGeoCodeResults.isNotEmpty()) {

                    initSearchUseCase(
                        startPlace = reverseGeoCodeResults[0]
                    )
                }
            }
        }
    }
}
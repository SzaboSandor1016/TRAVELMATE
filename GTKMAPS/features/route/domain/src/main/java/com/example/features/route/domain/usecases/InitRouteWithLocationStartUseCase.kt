package com.example.features.route.domain.usecases

/*
class InitRouteWithLocationStartUseCase(
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

                    val places = emptyList<PlaceSearchDomainModel>()

                    initSearchUseCase(
                        startPlace = reverseGeoCodeResults[0],
                        places = places
                    )
                }
            }
        }
    }
}*/

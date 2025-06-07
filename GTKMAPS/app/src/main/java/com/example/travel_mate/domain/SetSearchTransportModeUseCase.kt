package com.example.travel_mate.domain

class SetSearchTransportModeUseCase(
    private val searchOptionsRepository: SearchOptionsRepository
) {

    suspend operator fun invoke(index: Int) {

        searchOptionsRepository.setSearchTransportMode(
            index = index
        )
    }
}
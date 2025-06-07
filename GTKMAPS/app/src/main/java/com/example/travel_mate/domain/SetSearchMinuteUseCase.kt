package com.example.travel_mate.domain

class SetSearchMinuteUseCase(
    private val searchOptionsRepository: SearchOptionsRepository
) {

    suspend operator fun invoke(index: Int) {

        searchOptionsRepository.setMinute(
            index = index
        )
    }
}
package com.example.features.route.domain.usecases

import com.example.features.route.domain.models.PlaceRouteDomainModel
import com.example.features.route.domain.repositories.RouteRepository


class AddRemovePlaceToRouteUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(place: PlaceRouteDomainModel) {

        routeRepository.addRemovePlace(place = place)
    }
}
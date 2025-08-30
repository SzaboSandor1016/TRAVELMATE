package com.example.features.route.domain.usecases

import com.example.features.route.domain.repositories.RouteRepository

class ReorderRouteUseCase(
    private val routeRepository: RouteRepository
) {
    
    suspend operator fun invoke(newPosition: Int, placeUUID: String) {
        
        routeRepository.reorderRoute(
            newPosition = newPosition,
            placeUUID = placeUUID
        )
    }
}
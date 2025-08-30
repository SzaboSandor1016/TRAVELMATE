package com.example.features.search.domain.models.searchmodels

data class PlaceDataSearchDomainModel(
    val uUID: String,
    val name: String?,
    val coordinates: CoordinatesSearchDomainModel,
    val category: String,
) {
}
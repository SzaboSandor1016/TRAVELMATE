package com.example.features.search.domain.models.searchmodels

data class SearchStateSearchDomainModel(
    val search: SearchSearchDomainModel = SearchSearchDomainModel(),
    val currentPlace: PlaceSearchDomainModel? = null
    )
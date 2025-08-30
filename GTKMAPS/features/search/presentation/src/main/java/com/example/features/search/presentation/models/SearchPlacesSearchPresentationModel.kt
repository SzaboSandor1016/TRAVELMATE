package com.example.features.search.presentation.models

sealed interface SearchPlacesSearchPresentationModel {

    data object Empty: SearchPlacesSearchPresentationModel

    data class Searched(
        val places: List<PlaceSearchPresentationModel>
    ): SearchPlacesSearchPresentationModel
}
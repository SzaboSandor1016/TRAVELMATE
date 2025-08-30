package com.example.features.search.presentation.models

sealed interface SearchStartStatePresentationModel {

    data object Empty: SearchStartStatePresentationModel
    data class StartSelected(
        val searchInfo: SearchStartSearchPresentationModel
    ): SearchStartStatePresentationModel
}
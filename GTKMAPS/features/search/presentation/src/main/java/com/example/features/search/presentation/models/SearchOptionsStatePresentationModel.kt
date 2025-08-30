package com.example.features.search.presentation.models


sealed interface SearchOptionsStatePresentationModel {

    data object None: SearchOptionsStatePresentationModel
    data class Selected(val searchOptionsInfo: SearchOptionsInfoSearchPresentationModel): SearchOptionsStatePresentationModel
}
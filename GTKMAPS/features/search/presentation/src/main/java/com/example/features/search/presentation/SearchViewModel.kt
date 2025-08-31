package com.example.features.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.features.route.domain.usecases.InitRouteWithSelectedStartUseCase
import com.example.features.savetrip.domain.usecases.AreAssignablePlacesEmptyUseCase
import com.example.features.savetrip.domain.usecases.ClearAssignablePlacesUseCase
import com.example.features.search.domain.usecases.GetSearchOptionsUseCase
import com.example.features.search.domain.usecases.GetSearchStartUseCase
import com.example.features.search.domain.usecases.InitSearchWithLocationStartUseCase
import com.example.features.search.domain.usecases.InitSearchWithSelectedStartUseCase
import com.example.features.search.domain.usecases.RemovePlacesByCategoryUseCase
import com.example.features.search.domain.usecases.ResetSearchDetailsUseCase
import com.example.features.search.domain.usecases.ResetSearchOptionsUseCase
import com.example.features.search.domain.usecases.SearchAutocompleteUseCase
import com.example.features.search.domain.usecases.SearchPlacesUseCase
import com.example.features.search.domain.usecases.SetSearchMinuteUseCase
import com.example.features.search.domain.usecases.SetSearchTransportModeUseCase
import com.example.features.search.presentation.mappers.toPlaceSearchDomainModel
import com.example.features.search.presentation.mappers.toPlaceSearchPresentationModel
import com.example.features.search.presentation.mappers.toSearchInfoPresentationModel
import com.example.features.search.presentation.mappers.toSearchOptionsPresentationModel
import com.example.features.search.presentation.models.PlaceSearchPresentationModel
import com.example.features.search.presentation.models.SearchOptionsStatePresentationModel
import com.example.features.search.presentation.models.SearchStartStatePresentationModel
import com.example.navigation.NavigateToOuterDestinationUseCase
import com.example.navigation.OuterDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getSearchOptionsUseCase: GetSearchOptionsUseCase,
    private val getSearchStartUseCase: GetSearchStartUseCase,
    private val initSearchWithSelectedStartUseCase: InitSearchWithSelectedStartUseCase,
    private val initSearchWithLocationStartUseCase: InitSearchWithLocationStartUseCase,
    private val searchAutocompleteUseCase: SearchAutocompleteUseCase,
    private val searchPlacesUseCase: SearchPlacesUseCase,
    private val setSearchMinuteUseCase: SetSearchMinuteUseCase,
    private val setSearchTransportModeUseCase: SetSearchTransportModeUseCase,
    private val removePlacesByCategoryUseCase: RemovePlacesByCategoryUseCase,
    private val clearAssignablePlacesUseCase: ClearAssignablePlacesUseCase,
    private val resetSearchDetailsUseCase: ResetSearchDetailsUseCase,
    private val areAssignablePlacesEmptyUseCase: AreAssignablePlacesEmptyUseCase,
    private val navigateToOuterDestinationUseCase: NavigateToOuterDestinationUseCase,
    private val resetSearchOptionsUseCase: ResetSearchOptionsUseCase
): ViewModel() {

    private val _chipsState = MutableStateFlow(MainChipsState())
    val chipsState: StateFlow<MainChipsState> = _chipsState.asStateFlow()

    val tripButtonState: StateFlow<Boolean> by lazy {

        areAssignablePlacesEmptyUseCase().map {

            it
        }.flowOn(
            Dispatchers.Main
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )
    }

    val searchStartState: StateFlow<SearchStartStatePresentationModel> by lazy {
        getSearchStartUseCase().map { searchInfo ->

            if (searchInfo.name != null) {
                SearchStartStatePresentationModel.StartSelected(searchInfo.toSearchInfoPresentationModel())
            } else {
                SearchStartStatePresentationModel.Empty
            }
        }.flowOn(
            Dispatchers.Main
        ).stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
            SearchStartStatePresentationModel.Empty
        )
    }

    private val _searchAutocompleteState: MutableStateFlow<List<PlaceSearchPresentationModel>> =
        MutableStateFlow(emptyList())
    val searchAutocompleteState: StateFlow<List<PlaceSearchPresentationModel>> = _searchAutocompleteState.asStateFlow()

    init {

        viewModelScope.launch {

            combine(
                getSearchOptionsUseCase(),
                _chipsState
            ) { searchOptions, chipsState ->

                chipsState.copy(
                    extendedSearchSelected = searchOptions.parametersSelected,
                    transportModeSelected = searchOptions.transportSelected
                )

            }.collect { chipsState ->

                _chipsState.value = chipsState
            }
        }
    }

    fun searchAutocomplete(expression: String) {

        viewModelScope.launch {

            searchAutocompleteUseCase(expression).collect { results ->

                _searchAutocompleteState.update {

                    results.map { place ->
                        place.toPlaceSearchPresentationModel()
                    }
                }
            }
        }
    }

    fun resetAutocomplete() {

        viewModelScope.launch {

            _searchAutocompleteState.update {
                emptyList()
            }
        }
    }

    fun initSearchWith(startPlace: PlaceSearchPresentationModel) {

        viewModelScope.launch {

            initSearchWithSelectedStartUseCase(startPlace.toPlaceSearchDomainModel())
        }
    }

    fun initSearchWithLocation() {

        viewModelScope.launch {

            initSearchWithLocationStartUseCase()
        }
    }

    fun searchNearbyPlacesBy(
        content: String,
        category: String
    ) {

        viewModelScope.launch {

            searchPlacesUseCase(
                content = content,
                category = category
            )
        }
    }

    fun removePlacesByCategory(
        category: String
    ) {

        viewModelScope.launch {

            removePlacesByCategoryUseCase(
                category = category
            )
        }
    }

    fun setSearchMinute(indexOfOption: Int) {

        viewModelScope.launch {

            setSearchMinuteUseCase(
                index = indexOfOption
            )
        }
    }

    fun setSearchTransportMode(indexOfOption: Int) {

        viewModelScope.launch {

            setSearchTransportModeUseCase(
                index = indexOfOption
            )
        }
    }

    fun clearPlacesAddedToTrip() {

        viewModelScope.launch {

            clearAssignablePlacesUseCase()
        }
    }

    fun resetDetails(allDetails: Boolean) {

        viewModelScope.launch {

            resetSearchDetailsUseCase(
                allDetails = allDetails
            )
        }
    }

    fun navigateToUser() {

        viewModelScope.launch {

            navigateToOuterDestinationUseCase(OuterDestination.User)
        }
    }

    fun navigateToSave() {

        viewModelScope.launch {

            navigateToOuterDestinationUseCase(OuterDestination.Save)
        }
    }

    fun setExtendedSearchVisible(isExtended: Boolean) {
        _chipsState.update {
            it.copy(extendedSearchVisible = isExtended)
        }
    }

    fun resetExtendedSearch() {

        viewModelScope.launch {

            _chipsState.update {
                it.copy(extendedSearchVisible = false)
            }

            resetSearchOptionsUseCase()
        }
    }

    fun setCurrentChipGroup(id: Int, chipContent: List<FragmentSearch.ChipCategory>) {

        _chipsState.update {

            it.copy(currentChipGroup = id, currentChipGroupContent = chipContent.toList())
        }
    }

    fun setCurrentChipGroupContent( chipContent: List<FragmentSearch.ChipCategory>) {

        _chipsState.update {

            it.copy(currentChipGroupContent = chipContent.toList())
        }
    }

    fun resetCurrentChipGroup() {
        _chipsState.update {

            it.copy(currentChipGroup = null, currentChipGroupContent = null)
        }
    }

    fun addSelectedChip(
        index: Int
    ) {

        _chipsState.update { it ->
            val currentSelected = it.selectedChips.map { it }.plus(index)

            it.copy(selectedChips = currentSelected.toList())
        }
    }

    fun removeSelectedChip(
        index: Int
    ) {

        _chipsState.update {
            val currentSelected = it.selectedChips.map { it }.minus(index)

            it.copy(selectedChips = currentSelected.toList())
        }
    }

    data class MainChipsState(
        val extendedSearchVisible: Boolean = false,
        val extendedSearchSelected: Boolean = false,
        val transportModeSelected: Boolean = false,
        val currentChipGroup: Int? = null,
        val currentChipGroupContent: List<FragmentSearch.ChipCategory>? = null,
        val selectedChips: List<Int> = emptyList(),
        val distance: Double = 0.0,
        val transportMode: String? = null,
    ) {
        val showExtendedSearch = extendedSearchVisible && !extendedSearchSelected
    }
}
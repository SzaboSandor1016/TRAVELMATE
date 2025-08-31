package com.example.features.navigation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.features.navigation.domain.models.NavigationInfoNavigationDomainModel
import com.example.features.navigation.domain.usecases.EndNavigationUseCase
import com.example.features.navigation.domain.usecases.GetNavigationCurrentLocationUseCase
import com.example.features.navigation.domain.usecases.GetNavigationInfoUseCase
import com.example.features.navigation.domain.usecases.NavigateToPlaceInRouteUseCase
import com.example.features.navigation.presentation.mappers.toNavigationInfoPresentationModel
import com.example.features.navigation.presentation.models.NavigationInfoNavigationPresentationModel
import com.example.features.navigation.presentation.models.NavigationInfoStatePresentationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationViewModel(
    private val getNavigationInfoUseCase: GetNavigationInfoUseCase,
    private val navigateToPlaceInRouteUseCase: NavigateToPlaceInRouteUseCase,
    private val endNavigationUseCase: EndNavigationUseCase
): ViewModel() {

    val navigationInfo: StateFlow<NavigationInfoStatePresentationModel> by lazy {
        getNavigationInfoUseCase().map { navigationInfo ->

            when (navigationInfo) {
                is NavigationInfoNavigationDomainModel.Default -> {

                    NavigationInfoStatePresentationModel(
                        NavigationInfoNavigationPresentationModel.Default
                    )
                }

                is NavigationInfoNavigationDomainModel.NavigationInfo -> {

                    NavigationInfoStatePresentationModel(
                        navigationInfo.toNavigationInfoPresentationModel()
                    )
                }

                is NavigationInfoNavigationDomainModel.Arrived -> {
                    NavigationInfoStatePresentationModel(
                        navigationInfo.toNavigationInfoPresentationModel()
                    )
                }
            }
        }.flowOn(
            Dispatchers.Main
        ).stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
            NavigationInfoStatePresentationModel(
                NavigationInfoNavigationPresentationModel.Default
            )
        )
    }

    fun navigateToNextPlace() {

        viewModelScope.launch {

            navigateToPlaceInRouteUseCase()
        }
    }

    fun endNavigation() {

        viewModelScope.launch {

            endNavigationUseCase()
        }
    }
}
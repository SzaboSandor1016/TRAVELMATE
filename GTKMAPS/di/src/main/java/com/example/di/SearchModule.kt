package com.example.di

import com.example.features.search.data.repositories.SearchOptionsRepositoryImpl
import com.example.features.search.data.repositories.SearchRepositoryImpl
import com.example.features.search.domain.repositories.SearchOptionsRepository
import com.example.features.search.domain.repositories.SearchRepository
import com.example.features.search.domain.usecases.FindPlaceByUUIDInSearchUseCase
import com.example.features.search.domain.usecases.GetFullSearchStartUseCase
import com.example.features.search.domain.usecases.GetSearchOptionsUseCase
import com.example.features.search.domain.usecases.GetSearchPlacesDataUseCase
import com.example.features.search.domain.usecases.GetSearchStartDataUseCase
import com.example.features.search.domain.usecases.GetSearchStartUseCase
import com.example.features.search.domain.usecases.GetSelectedPlaceDataUseCase
import com.example.features.search.domain.usecases.InitSearchUseCase
import com.example.features.search.domain.usecases.InitSearchWithLocationStartUseCase
import com.example.features.search.domain.usecases.InitSearchWithSelectedStartUseCase
import com.example.features.search.domain.usecases.RemovePlacesByCategoryUseCase
import com.example.features.search.domain.usecases.ResetSearchDetailsUseCase
import com.example.features.search.domain.usecases.ResetSearchOptionsUseCase
import com.example.features.search.domain.usecases.SearchAutocompleteUseCase
import com.example.features.search.domain.usecases.SearchPlacesUseCase
import com.example.features.search.domain.usecases.SetSearchMinuteUseCase
import com.example.features.search.domain.usecases.SetSearchTransportModeUseCase
import com.example.features.search.presentation.SearchViewModel
import com.example.navigation.OuterNavigator
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module


val searchModule = module {

    single<SearchRepository> { SearchRepositoryImpl() }
    single<SearchOptionsRepository> { SearchOptionsRepositoryImpl() }

    factory { FindPlaceByUUIDInSearchUseCase(get()) }
    factory { GetSearchOptionsUseCase(get()) }
    factory { GetSearchPlacesDataUseCase(get()) }
    factory { GetSearchStartDataUseCase(get()) }
    factory { GetSelectedPlaceDataUseCase(get()) }
    factory { GetSearchStartUseCase(get()) }
    factory { InitSearchUseCase(get()) }
    factory { InitSearchWithSelectedStartUseCase(get()) }
    factory { InitSearchWithLocationStartUseCase(get(), get(), get()) }
    factory { RemovePlacesByCategoryUseCase(get()) }
    factory { ResetSearchDetailsUseCase(get()) }
    factory { SearchAutocompleteUseCase(get()) }
    factory { SearchPlacesUseCase(get(), get()) }
    factory { SetSearchMinuteUseCase(get()) }
    factory { SetSearchTransportModeUseCase(get()) }
    factory { GetFullSearchStartUseCase(get()) }
    factory { ResetSearchOptionsUseCase(get()) }

    viewModel { (outerNavigator: OuterNavigator) ->
        SearchViewModel(
            navigateToOuterDestinationUseCase = get {
                parametersOf(
                    outerNavigator
                )
            },
            getSearchOptionsUseCase = get(),
            getSearchStartUseCase = get(),
            initSearchWithSelectedStartUseCase = get(),
            initSearchWithLocationStartUseCase = get(),
            searchAutocompleteUseCase = get(),
            searchPlacesUseCase = get(),
            setSearchMinuteUseCase = get(),
            setSearchTransportModeUseCase = get(),
            removePlacesByCategoryUseCase = get(),
            clearAssignablePlacesUseCase = get(),
            resetSearchDetailsUseCase = get(),
            areAssignablePlacesEmptyUseCase = get(),
            resetSearchOptionsUseCase = get()
        )
    }
}
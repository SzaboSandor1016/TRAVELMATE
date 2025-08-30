package com.example.di

import com.example.features.navigation.data.repositories.NavigationRepositoryImpl
import com.example.features.navigation.domain.repositories.NavigationRepository
import com.example.features.navigation.domain.repositories.RouteNodeRepository
import com.example.features.navigation.domain.usecases.EndNavigationUseCase
import com.example.features.navigation.domain.usecases.GetNavigationCurrentLocationUseCase
import com.example.features.navigation.domain.usecases.GetNavigationInfoUseCase
import com.example.features.navigation.domain.usecases.GetNavigationMapDataUseCase
import com.example.features.navigation.domain.usecases.InitNavigationUseCase
import com.example.features.navigation.domain.usecases.NavigateToCustomPlaceUseCase
import com.example.features.navigation.domain.usecases.NavigateToPlaceInRouteUseCase
import com.example.features.navigation.presentation.NavigationViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val navigationModule = module {

    single<NavigationRepository> { NavigationRepositoryImpl() }
    single<RouteNodeRepository> { com.example.features.navigation.data.repositories.RouteNodeRepositoryImpl() }

    factory { EndNavigationUseCase(get()) }
    factory { GetNavigationCurrentLocationUseCase(get()) }
    factory { GetNavigationInfoUseCase(get()) }
    factory { GetNavigationMapDataUseCase(get()) }
    factory { InitNavigationUseCase(get(), get()) }
    factory { NavigateToCustomPlaceUseCase(get()) }
    factory { NavigateToPlaceInRouteUseCase(get()) }

    singleOf(::NavigationViewModel)
}
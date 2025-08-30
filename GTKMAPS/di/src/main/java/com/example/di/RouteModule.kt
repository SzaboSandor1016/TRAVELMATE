package com.example.di

import com.example.features.route.data.repositories.RouteNodeRepositoryImpl
import com.example.features.route.data.repositories.RouteRepositoryImpl
import com.example.features.route.domain.repositories.RouteNodeRepository
import com.example.features.route.domain.repositories.RouteRepository
import com.example.features.route.domain.usecases.AddRemovePlaceToRouteUseCase
import com.example.features.route.domain.usecases.GetCurrentRouteNodesCoordinatesUseCase
import com.example.features.route.domain.usecases.GetRouteInfoUseCase
import com.example.features.route.domain.usecases.GetRouteMapDataUseCase
import com.example.features.route.domain.usecases.GetRouteTransportModeUseCase
import com.example.features.route.domain.usecases.InitRouteUseCase
import com.example.features.route.domain.usecases.InitRouteWithSelectedStartUseCase
import com.example.features.route.domain.usecases.IsPlaceContainedByRouteUseCase
import com.example.features.route.domain.usecases.OptimizeRouteUseCase
import com.example.features.route.domain.usecases.RemovePlaceFromRouteUseCase
import com.example.features.route.domain.usecases.ReorderRouteUseCase
import com.example.features.route.domain.usecases.ResetRouteUseCase
import com.example.features.route.domain.usecases.SetRouteTransportModeUseCase
import com.example.features.route.presentation.RouteViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val routeModule = module {

    single<RouteRepository> { RouteRepositoryImpl() }
    single<RouteNodeRepository> { RouteNodeRepositoryImpl() }

    factory { AddRemovePlaceToRouteUseCase(get()) }
    factory { GetCurrentRouteNodesCoordinatesUseCase(get()) }
    factory { GetRouteInfoUseCase(get()) }
    factory { GetRouteMapDataUseCase(get()) }
    factory { GetRouteTransportModeUseCase(get()) }
    factory { InitRouteUseCase(get()) }
    factory { InitRouteWithSelectedStartUseCase(get()) }
    factory { IsPlaceContainedByRouteUseCase(get()) }
    factory { OptimizeRouteUseCase(get()) }
    factory { RemovePlaceFromRouteUseCase(get()) }
    factory { ReorderRouteUseCase(get()) }
    factory { ResetRouteUseCase(get()) }
    factory { SetRouteTransportModeUseCase(get()) }

    singleOf(::RouteViewModel)
}
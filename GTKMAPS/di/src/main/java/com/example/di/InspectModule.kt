package com.example.di

import com.example.features.inspect.data.repositories.CurrentTripRepositoryImpl
import com.example.features.inspect.domain.repositories.CurrentTripRepository
import com.example.features.inspect.domain.usecases.FindPlaceByUUIDInInspectUseCase
import com.example.features.inspect.domain.usecases.GetInspectedTripInfoUseCase
import com.example.features.inspect.domain.usecases.GetInspectedTripMapDataUseCase
import com.example.features.inspect.domain.usecases.GetInspectedTripSelectedPlaceDetailsUseCase
import com.example.features.inspect.domain.usecases.ResetInspectTripUseCase
import com.example.features.inspect.domain.usecases.SetInspectedTripUseCase
import com.example.features.inspect.domain.usecases.SetSelectedDayOfInspectedTripUseCase
import com.example.features.inspect.presentation.viewmodel.InspectTripViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val inspectModule = module {

    single<CurrentTripRepository> { CurrentTripRepositoryImpl() }
    singleOf(::InspectTripViewModel)

    factory { FindPlaceByUUIDInInspectUseCase(get()) }
    factory { GetInspectedTripInfoUseCase(get()) }
    factory { GetInspectedTripMapDataUseCase(get()) }
    factory { GetInspectedTripSelectedPlaceDetailsUseCase(get()) }
    factory { ResetInspectTripUseCase(get()) }
    factory { SetInspectedTripUseCase(get()) }
    factory { SetSelectedDayOfInspectedTripUseCase(get()) }
}
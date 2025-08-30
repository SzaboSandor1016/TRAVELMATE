package com.example.di

import com.example.features.findcustom.data.repositories.CustomPlaceRepositoryImpl
import com.example.features.findcustom.domain.repositories.CustomPlaceRepository
import com.example.features.findcustom.domain.usecases.GetCustomPlaceFullDataUseCase
import com.example.features.findcustom.domain.usecases.GetCustomPlaceInfoUseCase
import com.example.features.findcustom.domain.usecases.GetCustomPlaceMapDataUseCase
import com.example.features.findcustom.domain.usecases.ResetCustomPlaceUseCase
import com.example.features.findcustom.domain.usecases.SetCustomPlaceUseCase
import com.example.features.findcustom.presentation.viewmodels.CustomPlaceViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val findCustomModule = module{

    single<CustomPlaceRepository> { CustomPlaceRepositoryImpl() }
    factory { GetCustomPlaceFullDataUseCase(get()) }
    factory { GetCustomPlaceInfoUseCase(get()) }
    factory { GetCustomPlaceMapDataUseCase(get()) }
    factory { ResetCustomPlaceUseCase(get()) }
    factory { SetCustomPlaceUseCase(get(), get()) }

    singleOf(::CustomPlaceViewModel)
}
package com.example.di

import com.example.features.selectedplace.data.repository.SelectedPlaceRepositoryImpl
import com.example.features.selectedplace.domain.repository.SelectedPlaceRepository
import com.example.features.selectedplace.domain.usecases.GetSelectedPlaceFullDataUseCase
import com.example.features.selectedplace.domain.usecases.GetSelectedPlaceInfoUseCase
import com.example.features.selectedplace.domain.usecases.ResetSelectedPlaceUseCase
import com.example.features.selectedplace.domain.usecases.SetSelectedPlaceUseCase
import com.example.features.selectedplace.presentation.viewmodel.SelectedPlaceViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val selectedPlaceModule = module {

    single<SelectedPlaceRepository> { SelectedPlaceRepositoryImpl() }

    factory { GetSelectedPlaceFullDataUseCase(get()) }
    factory { GetSelectedPlaceInfoUseCase(get()) }
    factory { ResetSelectedPlaceUseCase(get()) }
    factory { SetSelectedPlaceUseCase(get()) }

    singleOf(::SelectedPlaceViewModel)
}
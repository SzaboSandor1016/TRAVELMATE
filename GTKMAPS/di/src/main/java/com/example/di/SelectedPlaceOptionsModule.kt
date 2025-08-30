package com.example.di

import com.example.features.selectedplaceoptions.data.repository.SelectedPlaceOptionsRepositoryImpl
import com.example.features.selectedplaceoptions.domain.repository.SelectedPlaceOptionsRepository
import com.example.features.selectedplaceoptions.domain.usecases.GetMainSelectedPlaceOptionsUseCase
import com.example.features.selectedplaceoptions.domain.usecases.GetSelectedPlaceSelectedPlaceOptionsAsFlowUseCase
import com.example.features.selectedplaceoptions.domain.usecases.SetMainContainerHeightUseCase
import com.example.features.selectedplaceoptions.domain.usecases.SetOriginOfSelectedPlaceUseCase
import com.example.features.selectedplaceoptions.domain.usecases.SetStateOfSelectedPlaceContainerUseCase
import org.koin.dsl.module


val selectedPlaceOptionsModule = module {

    single<SelectedPlaceOptionsRepository> { SelectedPlaceOptionsRepositoryImpl() }

    factory { GetMainSelectedPlaceOptionsUseCase(get()) }
    factory { GetSelectedPlaceSelectedPlaceOptionsAsFlowUseCase(get()) }
    factory { SetMainContainerHeightUseCase(get()) }
    factory { SetOriginOfSelectedPlaceUseCase(get()) }
    factory { SetStateOfSelectedPlaceContainerUseCase(get()) }
}
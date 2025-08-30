package com.example.di

import com.example.features.savetrip.data.repository.SaveTripRepositoryImpl
import com.example.features.savetrip.domain.repository.SaveTripRepository
import com.example.features.savetrip.domain.usecases.AddRemoveAssignablePlaceUseCase
import com.example.features.savetrip.domain.usecases.AreAssignablePlacesEmptyUseCase
import com.example.features.savetrip.domain.usecases.AssignPlaceToDayUseCase
import com.example.features.savetrip.domain.usecases.ClearAssignablePlacesUseCase
import com.example.features.savetrip.domain.usecases.GetAssignablePlacesUseCase
import com.example.features.savetrip.domain.usecases.GetPlacesOfSelectedDayUseCase
import com.example.features.savetrip.domain.usecases.GetSaveTripContributorsInfoUseCase
import com.example.features.savetrip.domain.usecases.GetSaveTripInfoUseCase
import com.example.features.savetrip.domain.usecases.GetSelectableContributorsInfoUseCase
import com.example.features.savetrip.domain.usecases.HasUserSignedInUseCase
import com.example.features.savetrip.domain.usecases.InitSaveBySelectingTripToUpdateUseCase
import com.example.features.savetrip.domain.usecases.InitSaveFromSearch
import com.example.features.savetrip.domain.usecases.IsContainedByTripUseCase
import com.example.features.savetrip.domain.usecases.RemoveDayFromTripUseCase
import com.example.features.savetrip.domain.usecases.RemovePlaceFromDayOfTripUseCase
import com.example.features.savetrip.domain.usecases.SaveTripUseCase
import com.example.features.savetrip.domain.usecases.SelectUnselectContributorUseCase
import com.example.features.savetrip.domain.usecases.SetDaysOfTripUseCase
import com.example.features.savetrip.domain.usecases.SetSaveTripDateUseCase
import com.example.features.savetrip.domain.usecases.SetSaveTripNoteUseCase
import com.example.features.savetrip.domain.usecases.SetSaveTripTitleUseCase
import com.example.features.savetrip.domain.usecases.SetSelectedDayOfSaveTripUseCase
import com.example.features.savetrip.domain.usecases.SetTripDetailsUseCase
import com.example.features.savetrip.domain.usecases.SetUserPermissionUseCase
import com.example.features.savetrip.presentation.viewmodel.SaveTripViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val saveTripModule = module {

    single<SaveTripRepository> { SaveTripRepositoryImpl(
        get(named("ApplicationScope"))
    ) }

    factory { HasUserSignedInUseCase(get()) }
    factory { AddRemoveAssignablePlaceUseCase(get()) }
    factory { AreAssignablePlacesEmptyUseCase(get()) }
    factory { AssignPlaceToDayUseCase(get()) }
    factory { ClearAssignablePlacesUseCase(get()) }
    factory { GetAssignablePlacesUseCase(get()) }
    factory { GetPlacesOfSelectedDayUseCase(get()) }
    factory { GetSaveTripContributorsInfoUseCase(get()) }
    factory { GetSelectableContributorsInfoUseCase( get()) }
    factory { GetSaveTripInfoUseCase(get()) }
    factory { InitSaveBySelectingTripToUpdateUseCase(get()) }
    factory { InitSaveFromSearch(get()) }
    factory { IsContainedByTripUseCase(get()) }
    factory { RemoveDayFromTripUseCase(get()) }
    factory { RemovePlaceFromDayOfTripUseCase(get()) }
    factory { SaveTripUseCase(get()) }
    factory { SelectUnselectContributorUseCase(get()) }
    factory { SetDaysOfTripUseCase(get()) }
    factory { SetSelectedDayOfSaveTripUseCase(get()) }
    factory { SetTripDetailsUseCase(get()) }
    factory { SetUserPermissionUseCase(get()) }

    factory { SetSaveTripNoteUseCase(get()) }
    factory { SetSaveTripTitleUseCase( get()) }
    factory { SetSaveTripDateUseCase(get()) }

    viewModelOf(::SaveTripViewModel)
}
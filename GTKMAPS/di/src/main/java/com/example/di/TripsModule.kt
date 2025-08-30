package com.example.di

import com.example.features.trips.data.repositories.TripRepositoryImpl
import com.example.features.trips.domain.repositories.TripRepository
import com.example.features.trips.domain.usecases.DeleteLocalTripUseCase
import com.example.features.trips.domain.usecases.DeleteRemoteTripUseCase
import com.example.features.trips.domain.usecases.DeleteTripUseCase
import com.example.features.trips.domain.usecases.GetLocalTripsUseCase
import com.example.features.trips.domain.usecases.GetRemoteContributedTripsUseCase
import com.example.features.trips.domain.usecases.GetRemoteTripsUseCase
import com.example.features.trips.domain.usecases.GetSelectedTripDataUseCase
import com.example.features.trips.presentation.TripsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module


val tripsModule = module {

    single<TripRepository> { TripRepositoryImpl(
        get(named("ApplicationScope"))
    ) }

    factory { DeleteLocalTripUseCase(get()) }
    factory { DeleteRemoteTripUseCase(get()) }
    //factory { DeleteTripsOfDeletedUserUseCase(get()) }
    factory { DeleteTripUseCase(get(), get()) }
    factory { GetLocalTripsUseCase(get()) }
    factory { GetRemoteTripsUseCase(get()) }
    factory { GetRemoteContributedTripsUseCase(get()) }
    factory { GetSelectedTripDataUseCase(get()) }

    viewModelOf(::TripsViewModel)
}
package com.example.di

import com.example.app.location.data.datasource.LocationLocalDataSourceImpl
import com.example.app.location.data.repository.LocationRepositoryImpl
import com.example.app.location.domain.datasource.LocationLocalDataSource
import com.example.app.location.domain.repository.LocationRepository
import com.example.app.location.domain.usecases.GetCurrentLocationUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val locationModule = module {

    single<LocationLocalDataSource> { LocationLocalDataSourceImpl(
        androidContext()
    ) }
    single<LocationRepository> { LocationRepositoryImpl( get()) }

    factory { GetCurrentLocationUseCase(get()) }
}

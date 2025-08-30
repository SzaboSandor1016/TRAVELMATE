package com.example.di

import com.example.core.remotedatasources.tripremotedatasource.data.datasource.FirebaseRemoteDataSourceImpl
import com.example.core.remotedatasources.tripremotedatasource.domain.datasource.FirebaseRemoteDataSource
import org.koin.dsl.module

val tripRemoteDataSourceModule = module {

    single<FirebaseRemoteDataSource> { FirebaseRemoteDataSourceImpl() }
}
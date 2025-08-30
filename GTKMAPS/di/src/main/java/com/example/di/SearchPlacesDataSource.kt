package com.example.di

import com.example.core.remotedatasources.searchplacesdatasource.data.OverpassRemoteDataSourceImpl
import com.example.core.remotedatasources.searchplacesdatasource.domain.OverpassRemoteDataSource
import org.koin.dsl.module

val searchPlacesDataSourceModule = module {

    single<OverpassRemoteDataSource> { OverpassRemoteDataSourceImpl() }
}
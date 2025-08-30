package com.example.di

import com.example.core.remotedatasources.searchstartdatasource.data.PhotonRemoteDataSourceImpl
import com.example.core.remotedatasources.searchstartdatasource.domain.PhotonRemoteDataSource
import org.koin.dsl.module

val searchStartDataSourceModule = module {

    single<PhotonRemoteDataSource> { PhotonRemoteDataSourceImpl() }
}
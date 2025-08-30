package com.example.di

import com.example.core.remotedatasources.routedatasource.data.RouteRemoteDataSourceImpl
import com.example.core.remotedatasources.routedatasource.domain.RouteRemoteDataSource

import org.koin.dsl.module

val routeDataSourceModule = module {

    single<RouteRemoteDataSource> { RouteRemoteDataSourceImpl() }
}
package com.example.di

import com.example.core.remotedatasources.reversegeocode.data.datasource.ReverseGeoCodeDataSourceImpl
import com.example.core.remotedatasources.reversegeocode.data.repository.ReverseGeoCodeRepositoryImpl
import com.example.core.remotedatasources.reversegeocode.domain.datasource.ReverseGeoCodeDataSource
import com.example.core.remotedatasources.reversegeocode.domain.repository.ReverseGeoCodeRepository
import org.koin.dsl.module

val reverseGeoCodeModule = module {

    single<ReverseGeoCodeDataSource> { ReverseGeoCodeDataSourceImpl() }
    single<ReverseGeoCodeRepository> { ReverseGeoCodeRepositoryImpl(get()) }
}
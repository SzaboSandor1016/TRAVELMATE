package com.example.features.user.presentation.di

import com.example.features.user.presentation.viewmodel.ViewModelUser
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val module = module {
    singleOf(::ViewModelUser)
}
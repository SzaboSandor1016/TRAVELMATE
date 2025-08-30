package com.example.di

import com.example.core.auth.data.FirebaseAuthenticationSourceImpl
import com.example.core.auth.domain.FirebaseAuthenticationSource
import org.koin.dsl.module

val authModule = module {

    single<FirebaseAuthenticationSource> { FirebaseAuthenticationSourceImpl() }
}
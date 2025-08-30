package com.example.di

import com.example.features.user.data.repositories.UserRepositoryImpl
import com.example.features.user.domain.repositories.UserRepository
import com.example.features.user.domain.usecases.DeleteUserUseCase
import com.example.features.user.domain.usecases.GetCurrentUserDataUseCase
import com.example.features.user.domain.usecases.SignInUserUseCase
import com.example.features.user.domain.usecases.SignOutUserUseCase
import com.example.features.user.domain.usecases.SignUpUserUseCase
import com.example.features.user.presentation.viewmodel.ViewModelUser
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module


val userModule = module {

    single<UserRepository> { UserRepositoryImpl(
        get(named("ApplicationScope"))
    ) }

    //factory { UpdateUserUseCase(get()) }
    //factory { CheckUserUseCase(get(), get()) }
    factory { DeleteUserUseCase(get()) }
    factory { GetCurrentUserDataUseCase(get()) }
    //factory { GetNewContributorDataUseCase(get()) }
    //factory { GetUsersByUIDsUseCase(get()) }
    factory { SignInUserUseCase(get()) }
    factory { SignOutUserUseCase(get()) }
    factory { SignUpUserUseCase(get()) }

    singleOf(::ViewModelUser)
}
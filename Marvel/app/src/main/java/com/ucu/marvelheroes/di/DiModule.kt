package com.ucu.marvelheroes.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ucu.marvelheroes.auth.AuthViewModel
import com.ucu.marvelheroes.data.source.interfaces.IAuthRepository
import com.ucu.marvelheroes.data.source.repositories.AuthRepository
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class DiModule {
    companion object {
        val repositoriesModule = module {
            singleOf(::AuthRepository) { bind<IAuthRepository>() }
        }

        val viewModelsModule = module {
            viewModelOf(::AuthViewModel)
        }

        val thirdPartyModule = module {
            single { Firebase.auth }
        }
    }
}
package com.ucu.marvelheroes.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ucu.marvelheroes.auth.AuthViewModel
import com.ucu.marvelheroes.details.CharacterDetailViewModel
import com.ucu.marvelheroes.comicdetails.ComicDetailViewModel
import com.ucu.marvelheroes.data.api.ApiService
import com.ucu.marvelheroes.data.api.MarvelClient
import com.ucu.marvelheroes.home.HomeViewModel
import com.ucu.marvelheroes.data.source.interfaces.IAuthRepository
import com.ucu.marvelheroes.data.source.interfaces.ICharactersRepository
import com.ucu.marvelheroes.data.source.repositories.AuthRepository
import com.ucu.marvelheroes.data.source.repositories.CharactersRepository
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class DiModule {
    companion object {
        val repositoriesModule = module {
            singleOf(::AuthRepository) { bind<IAuthRepository>() }
            singleOf(::CharactersRepository) { bind<ICharactersRepository>() }
        }

        val viewModelsModule = module {
            viewModelOf(::AuthViewModel)
            viewModelOf(::HomeViewModel)
            viewModelOf(::CharacterDetailViewModel)
            viewModelOf(::ComicDetailViewModel)
        }

        val thirdPartyModule = module {
            single<FirebaseAuth> { Firebase.auth }
        }

        val apiModule = module {
            single<ApiService> { MarvelClient.service }
        }
    }
}
package com.ucu.marvelheroes

import android.app.Application
import com.ucu.marvelheroes.di.DiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                DiModule.repositoriesModule,
                DiModule.viewModelsModule,
                DiModule.thirdPartyModule,
                DiModule.apiModule
            )
        }
    }
}
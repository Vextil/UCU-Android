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
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@App)
            // Load modules
            modules(DiModule.repositoriesModule, DiModule.viewModelsModule, DiModule.thirdPartyModule)
        }
    }
}
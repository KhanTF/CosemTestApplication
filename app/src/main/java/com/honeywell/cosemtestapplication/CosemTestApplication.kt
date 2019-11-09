package com.honeywell.cosemtestapplication

import android.app.Application
import com.honeywell.cosemtestapplication.koin.prepareCosemModule
import com.honeywell.cosemtestapplication.koin.prepareViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

class CosemTestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CosemTestApplication)
            loadKoinModules(getModules())
        }
    }

    private fun getModules() = listOf(prepareCosemModule(), prepareViewModelModule())

}
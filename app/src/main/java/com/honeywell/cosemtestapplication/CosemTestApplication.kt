package com.honeywell.cosemtestapplication

import android.app.Application
import com.honeywell.cosemtestapplication.koin.prepareCommonModule
import com.honeywell.cosemtestapplication.koin.prepareCosemModule
import com.honeywell.cosemtestapplication.koin.prepareViewModelModule
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import timber.log.Timber

class CosemTestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler {
            Timber.e(it)
            it.printStackTrace()
        }
        startKoin {
            androidContext(this@CosemTestApplication)
            loadKoinModules(getModules())
        }
    }

    private fun getModules() = listOf(prepareCosemModule(), prepareViewModelModule(), prepareCommonModule())

}
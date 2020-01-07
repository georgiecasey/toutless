package com.georgiecasey.toutless

import android.app.Application
import timber.log.Timber

class ToutlessApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
package com.thinkgas.heatapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TpiApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
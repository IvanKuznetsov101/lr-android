package com.vsu.test.di

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import com.vsu.test.BuildConfig
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TestApp : Application(){
    @Inject lateinit var workerFactory: HiltWorkerFactory
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
    }
}
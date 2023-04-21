package ru.netology.nework

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(){
    override fun onCreate() {
        MapKitFactory.setApiKey(API_KEY)
        super.onCreate()
    }
}
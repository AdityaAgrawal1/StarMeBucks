package com.example.starmebucks

import android.app.Application
import com.example.starmebucks.utils.ApiKeyProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StarbucksApp: Application(){
    override fun onCreate() {
        super.onCreate()
        ApiKeyProvider.init(this)
    }
}
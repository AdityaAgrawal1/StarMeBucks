package com.example.starmebucks.utils

import android.content.Context
import com.example.starmebucks.R

object ApiKeyProvider {
    lateinit var mapsApiKey: String
        private set

    fun init(context: Context) {
        if (!::mapsApiKey.isInitialized) {
            mapsApiKey = context.getString(R.string.maps_api_key)
        }
    }
}

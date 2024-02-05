package com.example.starmebucks.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _latitude = MutableLiveData(0.0)
    var latitude:LiveData<Double> = _latitude
    private val _longitude = MutableLiveData(0.0)
    var longitude:LiveData<Double> = _longitude

    fun setLatLong(lat: Double, long: Double){
        _latitude.value = lat
        _longitude.value = long
    }

    private val _locationPermissionGranted = MutableLiveData<Boolean>()
    val locationPermissionGranted: LiveData<Boolean> = _locationPermissionGranted

    fun setLocationPermissionGranted(isGranted: Boolean) {
        _locationPermissionGranted.value = isGranted
    }
}

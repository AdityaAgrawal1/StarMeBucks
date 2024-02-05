package com.example.starmebucks.model

data class Starbucks(
    val name: String,
    val distance: Float,
    val address: String,
    val imageUrl: String,
    val isOpen:String,
    val rating: Double,
    val lat: Double,
    val lng: Double,
)

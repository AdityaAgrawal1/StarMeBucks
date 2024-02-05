package com.example.starmebucks.network

import com.example.starmebucks.model.PlacesApiResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface StarbucksAPI {

    @GET("place/nearbysearch/json")
    suspend fun fetchNearbyStarbucks(
        @Query("location") location: String,
        @Query("radius") radius: String = "5000",
        @Query("type") type: String = "cafe",
        @Query("keyword") keyword: String = "Starbucks",
        @Query("key") apiKey: String
    ): PlacesApiResponse

}
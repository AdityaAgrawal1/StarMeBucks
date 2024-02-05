package com.example.starmebucks.repository

import com.example.starmebucks.model.PlacesApiResponse
import com.example.starmebucks.network.StarbucksAPI
import com.example.starmebucks.utils.ApiKeyProvider
import com.example.starmebucks.utils.resource.Resource
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ActivityScoped
class StarbucksRepository @Inject constructor(
    private val api:StarbucksAPI
){

    fun fetchNearbyStarbucks(latitude: Double, longitude: Double):
            Flow<Resource<PlacesApiResponse>> = flow {
        emit(Resource.Loading)

        val apiKey = ApiKeyProvider.mapsApiKey
        val location = "$latitude,$longitude"
        val radius = "50000" // Search within 50 km radius
        val type = "cafe"
        val keyword = "Starbucks" // Filter for Starbucks

        val response= api.fetchNearbyStarbucks(
            location,
            radius,
            type,
            keyword,
            apiKey
        )
        if(response.status == "OK"){
            emit(Resource.Success(response))
        }else {
            emit(Resource.Failure(true, "Failed to fetch data"))
        }
    }
}
package com.example.starmebucks.di

import com.example.starmebucks.network.StarbucksAPI
import com.example.starmebucks.repository.StarbucksRepository
import com.example.starmebucks.utils.constants.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    @Singleton
    @Provides
    fun provideStarbucksApi(): StarbucksAPI {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(client)
            .build()
            .create(StarbucksAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideStarbucksRepository(api:StarbucksAPI) =
        StarbucksRepository(api)

}
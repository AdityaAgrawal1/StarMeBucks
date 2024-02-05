package com.example.starmebucks.utils.resource

sealed class Resource<out T> {

    data class Success<out T>(val value: T) : Resource<T>()

    data class Failure(
        val isNetworkError: Boolean,
        val errorMsg: String?
    ) : Resource<Nothing>()

    object Loading : Resource<Nothing>()
}
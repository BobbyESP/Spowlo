package com.bobbyesp.utilities

sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T?) : Resource<T>()
    data class Error<T>(val message: String) : Resource<Nothing>()
}
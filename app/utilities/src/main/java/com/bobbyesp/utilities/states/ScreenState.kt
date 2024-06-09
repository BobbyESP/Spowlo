package com.bobbyesp.utilities.states

sealed class ScreenState<out T> {
    data object Loading : ScreenState<Nothing>()
    data class Success<T>(val data: T?) : ScreenState<T>()
    data class Error(val exception: Exception) : ScreenState<Nothing>()
}
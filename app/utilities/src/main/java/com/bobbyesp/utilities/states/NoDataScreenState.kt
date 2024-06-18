package com.bobbyesp.utilities.states

sealed class NoDataScreenState {
    data object Loading : NoDataScreenState()
    data object Success : NoDataScreenState()
    data class Error(val exception: Exception) : NoDataScreenState()
}
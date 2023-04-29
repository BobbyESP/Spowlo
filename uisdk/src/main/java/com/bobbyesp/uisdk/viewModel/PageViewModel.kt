package com.bobbyesp.uisdk.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
abstract class PageViewModel <T> : ViewModel() {
    var state by mutableStateOf<State<T>>(State.Loading())
        private set

    val data: T? get() = (state as? State.Loaded<T>)?.data

    abstract suspend fun load(): T

    fun reload() {
        state = State.Loading()
        viewModelScope.launch { loadInternal() }
    }

    fun setState(data: T) {
        state = State.Loaded(data)
    }

    private suspend fun loadInternal() {
        state = try {
            State.Loaded(data = load())
        } catch (e: Throwable) {
            e.printStackTrace()
            State.Error(exception = e)
        }
    }

    sealed class State <T> {
        class Loaded <T> (
            val data: T
        ): State<T>()

        class Error <T> (
            val exception: Throwable
        ): State<T>()

        class Loading <T> : State<T>()
    }
}
package com.bobbyesp.spowlo.ui.pages.home

import android.content.Context
import androidx.lifecycle.ViewModel
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.SpotifyApiRequests
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.checkIfLoggedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor() : ViewModel() {
    private val api = SpotifyApiRequests

    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val state: HomePageState = HomePageState.Loading,
        val loggedIn: Boolean = false
    )

    fun checkSpotifyApiIsValid(context: Context) {
        mutablePageViewState.update { it.copy(loggedIn = checkIfLoggedIn(context)) }
    }
}

sealed class HomePageState {
    object Loading : HomePageState()
    object Error : HomePageState()
    object Success : HomePageState()
}
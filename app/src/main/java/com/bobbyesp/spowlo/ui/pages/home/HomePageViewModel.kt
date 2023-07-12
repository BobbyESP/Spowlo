package com.bobbyesp.spowlo.ui.pages.home

import androidx.lifecycle.ViewModel
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.SpotifyApiRequests
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor() : ViewModel() {
    private val api = SpotifyApiRequests

    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val state: HomePageState = HomePageState.Loading,
    )

}

sealed class HomePageState {
    object Loading : HomePageState()
    object Error : HomePageState()
    object Success : HomePageState()
}
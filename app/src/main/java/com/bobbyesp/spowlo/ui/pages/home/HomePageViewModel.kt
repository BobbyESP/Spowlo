package com.bobbyesp.spowlo.ui.pages.home

import androidx.lifecycle.ViewModel
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.SpotifyPkceLoginImpl
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.ActivityCallsShortener
import com.bobbyesp.spowlo.ui.util.pages.PageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor() : ViewModel() {

    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()
    private val activityWrapper = ActivityCallsShortener(MainActivity.getActivity())

    data class PageViewState(
        val state: PageState = PageState.Loading,
        val loggedIn: Boolean = false
    )

    fun login() {
        activityWrapper.execute {
            startSpotifyClientPkceLoginActivity(SpotifyPkceLoginImpl::class.java)
        }
    }
}
package com.bobbyesp.spowlo.ui.pages.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.SpotifyPkceLoginImpl
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.ActivityCallsShortener
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.SpotifyAuthManager
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.isLogged
import com.bobbyesp.spowlo.utils.ui.pages.PageStateWithThrowable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class HomePageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val spotifyAuthManager: SpotifyAuthManager
) : ViewModel() {
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()
    private val activityWrapper = ActivityCallsShortener(MainActivity.getActivity())

    data class PageViewState(
        val state: PageStateWithThrowable = PageStateWithThrowable.Loading,
        val loggedIn: Boolean = false
    )

    init {
        getLoggedIn()
    }

    fun login() {
        try {
            activityWrapper.execute {
                startSpotifyClientPkceLoginActivity(SpotifyPkceLoginImpl::class.java)
            }
        } catch (e: Exception) {
            Log.e("HomePageViewModel", "Error logging in", e)
            deleteEncryptedSharedPrefs()
        }
    }

    private fun getLoggedIn() {
        val logged = isLogged(context)
        mutablePageViewState.update {
            it.copy(loggedIn = logged)
        }
    }

    fun deleteEncryptedSharedPrefs() {
        spotifyAuthManager.deleteCredentials()
    }
}
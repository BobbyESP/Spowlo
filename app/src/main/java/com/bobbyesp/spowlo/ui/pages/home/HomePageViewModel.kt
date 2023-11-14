package com.bobbyesp.spowlo.ui.pages.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.bobbyesp.library.SpotDL
import com.bobbyesp.library.SpotDLRequest
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.SpotifyPkceLoginImpl
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.ActivityCallsShortener
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.SpotifyAuthManager
import com.bobbyesp.spowlo.utils.ui.pages.PageStateWithThrowable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
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
        viewModelScope.launch {
            getLoggedIn()
        }
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

    suspend fun getLoggedIn() {
        val logged = spotifyAuthManager.isAuthenticated()
        mutablePageViewState.update {
            it.copy(loggedIn = logged)
        }
    }

    fun testSpotDL() {
        viewModelScope.launch(Dispatchers.Default) {
            val downloadDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Spowlo"
            )
            val request = SpotDLRequest(urls = emptyList())
            request.addOption("download", "alan walker faded")
            request.addOption("--log-level", "DEBUG")
            //request.addOption("--simple-tui")
            request.addOption("--output", downloadDir.absolutePath)
            request.addOption("--client-id", "abcad8ba647d4b0ebae797a8f444ac9b")
            request.addOption("--client-secret", "7ac6711e50044f1db20e4610f10f1f98")

            //Print every command
            for (s in request.buildCommand()) Log.d("LOL", s)

            SpotDL.getInstance().execute(request, null, callback = { _, _, output ->
                Log.d("SpotDL Test", output)
            })
        }
    }

    fun deleteEncryptedSharedPrefs() {
        spotifyAuthManager.deleteCredentials()
    }
}
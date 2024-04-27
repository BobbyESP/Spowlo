package com.bobbyesp.spowlo.ui.pages

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.SpotifyPkceLoginImpl
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.ActivityCallsShortener
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.SpotifyAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class LoginManagerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val spotifyAuthManager: SpotifyAuthManager
) : ViewModel() {
    private val mutableLoginManagerState = MutableStateFlow(LoginManagerState())
    val pageViewState = mutableLoginManagerState.asStateFlow()

    private val activityWrapper = ActivityCallsShortener(MainActivity.getActivity())

    data class LoginManagerState(
        val isTryingToLogin: Boolean = false,
        val loginState: LoginState = LoginState.CHECKING_STATUS
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getLoggedIn(viewModelScope)
        }
    }

    fun login() {
        try {
            updateLoginState(true)
            activityWrapper.execute {
                startSpotifyClientPkceLoginActivity(SpotifyPkceLoginImpl::class.java)
            }
            updateLoginState(false)
        } catch (e: Exception) {
            Log.e("HomePageViewModel", "Error logging in", e)
            updateLoginState(false)
            deleteEncryptedSharedPrefs()
        } finally {
            viewModelScope.launch {
                getLoggedIn(viewModelScope)
            }
        }
    }

    suspend fun getLoggedIn(scope: CoroutineScope) {
        updateLoggedInState(LoginState.CHECKING_STATUS)
        val logged = scope.async { isLogged() }.await()
        updateLoggedInState(logged)
    }

    suspend fun isLogged(): LoginState {
        return LoginState.fromBoolean(spotifyAuthManager.isAuthenticated())
    }

    private fun deleteEncryptedSharedPrefs() {
        spotifyAuthManager.deleteCredentials()
    }

    private fun updateLoginState(isTryingToLogin: Boolean) {
        mutableLoginManagerState.update {
            it.copy(isTryingToLogin = isTryingToLogin)
        }
    }

    private fun updateLoggedInState(state: LoginState) {
        mutableLoginManagerState.update {
            it.copy(loginState = state)
        }
    }
}

enum class LoginState {
    CHECKING_STATUS,
    LOGGED_IN,
    NOT_LOGGED_IN;

    companion object {
        fun fromBoolean(logged: Boolean): LoginState {
            return if (logged) LOGGED_IN else NOT_LOGGED_IN
        }
    }
}
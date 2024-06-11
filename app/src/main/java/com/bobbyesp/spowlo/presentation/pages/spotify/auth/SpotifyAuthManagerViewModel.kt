package com.bobbyesp.spowlo.presentation.pages.spotify.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.SpotifyClientApi
import com.bobbyesp.spowlo.features.spotify.auth.CredentialsStorer
import com.bobbyesp.spowlo.features.spotify.auth.SpotifyAuthManager
import com.bobbyesp.spowlo.features.spotify.auth.SpotifyAuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpotifyAuthManagerViewModel @Inject constructor(
    private val credentialsStore: CredentialsStorer
) : SpotifyAuthManager, ViewModel() {
    private val mutableAuthManagerState: MutableStateFlow<SpotifyAuthState> =
        MutableStateFlow(SpotifyAuthState.LoggingIn(true))
    val authManagerViewState = mutableAuthManagerState.asStateFlow()

    fun updateAuthenticationState(state: SpotifyAuthState) {
        mutableAuthManagerState.update { state }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (isAuthenticated()) {
                updateAuthenticationState(SpotifyAuthState.Authenticated)
            } else {
                updateAuthenticationState(SpotifyAuthState.NotAuthenticated)
            }
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return try {
            val isTokenValid =
                credentialsStore.pkceApi()?.isTokenValid()?.isValid ?: false
            isTokenValid
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun shouldRefreshToken(): Boolean {
        return credentialsStore.pkceApi()?.token?.shouldRefresh() ?: true
    }

    override suspend fun refreshToken() {
        try {
            credentialsStore.pkceApi()?.refreshToken()
        } catch (e: Exception) {
            updateAuthenticationState(SpotifyAuthState.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun logout() {
        try {
            credentialsStore.getCredentials().clear()
            updateAuthenticationState(SpotifyAuthState.NotAuthenticated)
        } catch (e: Exception) {
            updateAuthenticationState(SpotifyAuthState.Error(e.message ?: "An unknown error occurred"))
        }
    }

    private suspend fun CredentialsStorer.pkceApi(): SpotifyClientApi? {
        return this.getCredentials().getSpotifyClientPkceApi()
    }
}

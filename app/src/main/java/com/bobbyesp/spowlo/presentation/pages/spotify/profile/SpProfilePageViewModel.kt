package com.bobbyesp.spowlo.presentation.pages.spotify.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.SpotifyClientApi
import com.bobbyesp.spowlo.features.spotify.auth.CredentialsStorer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpProfilePageViewModel @Inject constructor(
    private val credentialsStore: CredentialsStorer
) : ViewModel() {
    private lateinit var clientApi: SpotifyClientApi

    init {
        viewModelScope.launch {
            clientApi = credentialsStore.getCredentials().getSpotifyClientPkceApi()
                ?: throw IllegalStateException("Client API is not initialized - I the user not authenticated?")
            clientApi.player.resume()
        }
    }
}
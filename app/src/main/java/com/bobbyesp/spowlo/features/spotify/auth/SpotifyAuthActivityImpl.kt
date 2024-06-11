package com.bobbyesp.spowlo.features.spotify.auth

import com.adamratzman.spotify.SpotifyClientApi
import com.bobbyesp.spowlo.MainActivity

class SpotifyAuthActivityImpl: SpotifyAuthActivity() {
    private val authManagerViewModel = MainActivity.getActivity().authManagerViewModel

    override fun onSuccess(api: SpotifyClientApi) {
        authManagerViewModel.updateAuthenticationState(SpotifyAuthState.Authenticated)
    }

    override fun onFailure(exception: Exception) {
        authManagerViewModel.updateAuthenticationState(
            SpotifyAuthState.Error(
                exception.message ?: "An unknown error occurred"
            )
        )
    }

    override fun setLoadingState(isLoading: Boolean): () -> Unit {
        authManagerViewModel.updateAuthenticationState(SpotifyAuthState.LoggingIn(isLoading))
        return super.setLoadingState(isLoading)
    }
}
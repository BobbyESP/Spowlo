package com.bobbyesp.spowlo.features.spotify.auth

sealed class SpotifyAuthState {
    data object NotAuthenticated : SpotifyAuthState()
    data class LoggingIn(val isLoading: Boolean) : SpotifyAuthState()
    data object Authenticated : SpotifyAuthState()
    data class Error(val message: String) : SpotifyAuthState()
}
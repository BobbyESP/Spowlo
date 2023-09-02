package com.bobbyesp.spowlo.features.spotifyApi.utils.login

import com.adamratzman.spotify.SpotifyClientApi

interface SpotifyAuthManager {
    fun launchLoginActivity()
    fun getSpotifyClientApi(): SpotifyClientApi?
    suspend fun isAuthenticated(): Boolean
    fun shouldRefreshToken(): Boolean
    suspend fun refreshToken(): Boolean
    fun credentialsFileExists(): Boolean
    fun deleteCredentials(): Boolean
}
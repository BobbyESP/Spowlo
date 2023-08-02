package com.bobbyesp.spowlo.features.spotifyApi.utils.login

import com.adamratzman.spotify.SpotifyClientApi

interface SpotifyAuthManager {
    fun launchLoginActivity()
    fun getSpotifyClientApi(): SpotifyClientApi?
    suspend fun isAuthenticated(): Boolean
    suspend fun refreshToken(): Boolean
    fun deleteCredentials(): Boolean
}
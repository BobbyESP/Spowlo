package com.bobbyesp.spowlo.features.spotify.auth

interface SpotifyAuthManager {
    suspend fun isAuthenticated(): Boolean
    suspend fun shouldRefreshToken(): Boolean
    suspend fun refreshToken()
    suspend fun logout()
}
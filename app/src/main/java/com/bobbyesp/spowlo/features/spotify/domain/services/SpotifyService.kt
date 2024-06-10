package com.bobbyesp.spowlo.features.spotify.domain.services

import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.Token

interface SpotifyService {
    suspend fun getSpotifyApi(): SpotifyAppApi
    suspend fun getSpotifyToken(): Token
}
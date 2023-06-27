package com.bobbyesp.spowlo.features.spotifyApi.data.remote

import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.SpotifyPublicUser

class UserSearch(private val spotifyApi: SpotifyAppApi) {
    suspend fun search(userQuery: String): SpotifyPublicUser? {
        return spotifyApi.users.getProfile(userQuery)
    }
}

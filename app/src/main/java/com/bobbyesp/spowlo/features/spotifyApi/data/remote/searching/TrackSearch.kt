package com.bobbyesp.spowlo.features.spotifyApi.data.remote.searching

import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.Track

class TrackSearch(private val spotifyApi: SpotifyAppApi) {
    suspend fun search(trackQuery: String): List<Track> {
        runCatching {
            spotifyApi.search.searchTrack(trackQuery, limit = 50).items
        }.onFailure {
            it.printStackTrace()
            return emptyList()
        }.onSuccess {
            return it
        }
        return emptyList()
    }

    suspend fun search(songName: String, artistName: String): List<Track> {
        runCatching {
            spotifyApi.search.searchTrack("$songName $artistName").items
        }.onFailure {
            it.printStackTrace()
            return emptyList()
        }.onSuccess {
            return it
        }
        return emptyList()
    }
}
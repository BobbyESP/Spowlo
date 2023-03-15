package com.bobbyesp.spowlo.features.spotify_api

import android.util.Log
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.Playlist
import com.adamratzman.spotify.models.SpotifyPublicUser
import com.adamratzman.spotify.models.SpotifySearchResult
import com.adamratzman.spotify.models.Token
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.spotifyAppApi
import com.adamratzman.spotify.utils.Market
import com.bobbyesp.spowlo.BuildConfig
import kotlinx.coroutines.Job

object SpotifyApiRequests {

    private val clientId = BuildConfig.CLIENT_ID
    private val clientSecret = BuildConfig.CLIENT_SECRET
    private var api: SpotifyAppApi? = null
    private var token: Token? = null

    private var currentJob: Job? = null


    //Pulls the clientId and clientSecret tokens and builds them into an object

    suspend fun buildApi() {
        Log.d(
            "SpotifyApiRequests",
            "Building API with client ID: $clientId and client secret: $clientSecret"
        )
        token = spotifyAppApi(clientId, clientSecret).build().token
        api = spotifyAppApi(clientId, clientSecret, token!!) {
            automaticRefresh = true
        }.build()
    }

    //Performs Spotify database query for queries related to user information.
    suspend fun userSearch(userQuery: String): SpotifyPublicUser? {
        return api!!.users.getProfile(userQuery)
    }

    // Performs Spotify database query for queries related to track information.
    suspend fun trackSearch(searchQuery: String): SpotifySearchResult {
        kotlin.runCatching {
            api!!.search.searchAllTypes(searchQuery, limit = 50, offset = 1, market = Market.ES)
        }.onFailure {
            Log.d("SpotifyApiRequests", "Error: ${it.message}")
            return SpotifySearchResult()
        }.onSuccess {
            return it
        }
        return SpotifySearchResult()
    }

    suspend fun searchAllTypes(searchQuery: String): SpotifySearchResult {
        kotlin.runCatching {
            api!!.search.searchAllTypes(searchQuery, limit = 50, offset = 1, market = Market.ES)
        }.onFailure {
            Log.d("SpotifyApiRequests", "Error: ${it.message}")
            return SpotifySearchResult()
        }.onSuccess {
            return it
        }
        return SpotifySearchResult()
    }

    //search by id
    suspend fun searchPlaylistById(id: String): Playlist? {
        kotlin.runCatching {
            api!!.playlists.getPlaylist(id, market = Market.ES)
        }.onFailure {
            Log.d("SpotifyApiRequests", "Error: ${it.message}")
            return null
        }.onSuccess {
            return it
        }
        return null
    }

    suspend fun searchTrackById(id: String): Track? {
        kotlin.runCatching {
            api!!.tracks.getTrack(id, market = Market.ES)
        }.onFailure {
            Log.d("SpotifyApiRequests", "Error: ${it.message}")
            return null
        }.onSuccess {
            return it
        }
        return null
    }
}
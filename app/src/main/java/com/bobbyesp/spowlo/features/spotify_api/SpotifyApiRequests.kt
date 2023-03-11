package com.bobbyesp.spowlo.features.spotify_api

import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.SpotifySearchResult
import com.adamratzman.spotify.spotifyAppApi
import com.adamratzman.spotify.utils.Market
import com.bobbyesp.spowlo.BuildConfig

class SpotifyApiRequests {

    private val clientId = BuildConfig.CLIENT_ID
    private val clientSecret = BuildConfig.CLIENT_SECRET

    private var api: SpotifyAppApi? = null

    suspend fun buildApi(): SpotifyAppApi {
        if (api == null) {
            api = spotifyAppApi(
                clientId = clientId,
                clientSecret = clientSecret
            ).build()
        }
        return api!!
    }

    suspend fun searchForTrack(query: String): SpotifySearchResult {
        return api!!.search.search(query, limit = 50, offset = 1, market = Market.ES)
    }
}
package com.bobbyesp.spowlo.data.remote.spotify_bridge

import com.bobbyesp.spowlo.domain.spotify_bridge.model.APIResponse
import retrofit2.Response
import retrofit2.http.GET

interface SpotifyBridgeAPI {

    @GET("/api/v1/spottoyt?song={spotifyLink}")
    suspend fun getYTApiResponse(): Response<APIResponse>

    companion object {
        const val BASE_URL = "https://spowlo-js-api.onrender.com"
    }
}
package com.bobbyesp.spowlo.features.spotify.data.remote

import android.util.Log
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.SpotifyException
import com.adamratzman.spotify.models.Token
import com.adamratzman.spotify.spotifyAppApi
import com.bobbyesp.spowlo.features.spotify.domain.services.SpotifyService
import com.bobbyesp.utilities.Logging.isDebug
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class SpotifyServiceImpl: SpotifyService, KoinComponent {
    private val clientId: String by inject(named("client_id"))
    private val clientSecret: String by inject(named("client_secret"))

    private var token: Token? = null
    private var api: SpotifyAppApi? = null

    private var recursionDepth = 0
    private val MAX_RECURSION_DEPTH = 3

    override suspend fun getSpotifyApi(): SpotifyAppApi {
        if (api == null) {
            buildApi()
        }
        return api ?: throw IllegalStateException("Spotify API is null")
    }

    override suspend fun getSpotifyToken(): Token {
        if (token == null) {
            buildApi()
        }
        return token ?: throw IllegalStateException("Spotify Token is null")
    }

    /**
     * This method is responsible for building the Spotify API.
     * It first checks if the application is in debug mode, and if so, logs the client ID and secret.
     * Then, it attempts to build the Spotify API with the provided client ID and secret.
     * If the API is successfully built, it retrieves the token from the API and stores it.
     *
     * If an exception occurs during the building of the API, it logs the error.
     * If a BadRequestException occurs, it checks if the token should be refreshed and if the recursion depth is less than the maximum allowed.
     * If both conditions are met, it logs the information, clears the API, and attempts to build the API again, incrementing the recursion depth.
     *
     * @throws Exception if there is an error building the API.
     * @throws SpotifyException.BadRequestException if a bad request is made to the Spotify API.
     */
    private suspend fun buildApi() {
        try {
            if (isDebug) Log.d(
                "SpotifyApiRequests",
                "Building API with client ID: $clientId and client secret: $clientSecret"
            )
            api = spotifyAppApi(clientId, clientSecret).build().apply {
                with(this.spotifyApiOptions) {
                    automaticRefresh = true
                    enableDebugMode = isDebug
                }
            }
            token = api?.token
        } catch (e: Exception) {
            if (isDebug) Log.e("SpotifyApiRequests", "Error building API", e)
        } catch (e: SpotifyException.BadRequestException) {
            token?.let {
                if (it.shouldRefresh() && recursionDepth < MAX_RECURSION_DEPTH) {
                    Log.i(
                        "SpotifyApiRequests",
                        "Token expired, refreshing token; recursion depth: $recursionDepth of $MAX_RECURSION_DEPTH"
                    )
                    clearApi()
                    buildApi()
                    recursionDepth++
                    return@let
                }
            }
        }
    }

    private fun clearApi() {
        api = null
        token = null
    }
}
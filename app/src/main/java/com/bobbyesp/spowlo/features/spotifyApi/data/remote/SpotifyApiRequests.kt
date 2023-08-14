package com.bobbyesp.spowlo.features.spotifyApi.data.remote

import android.util.Log
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.SpotifyException
import com.adamratzman.spotify.models.Token
import com.adamratzman.spotify.spotifyAppApi
import com.bobbyesp.spowlo.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpotifyApiRequests {
    private val isDebug = BuildConfig.DEBUG
    private const val clientId = BuildConfig.CLIENT_ID
    private const val clientSecret = BuildConfig.CLIENT_SECRET
    private var api: SpotifyAppApi? = null
    private var token: Token? = null

    private var recursionDepth = 0
    private const val MAX_RECURSION_DEPTH = 3

    private suspend fun buildApi() {
        try {
            if (isDebug) Log.d(
                "SpotifyApiRequests",
                "Building API with client ID: $clientId and client secret: $clientSecret"
            )
            token = spotifyAppApi(clientId, clientSecret).build().token
            api = spotifyAppApi(clientId, clientSecret, token!!) {
                automaticRefresh = true
            }.build()
        } catch (e: Exception) {
            if (isDebug) Log.e("SpotifyApiRequests", "Error building API", e)
        } catch (e: SpotifyException.BadRequestException) {
            if (token != null && token!!.shouldRefresh() && recursionDepth < MAX_RECURSION_DEPTH) {
                clearApi()
                buildApi()
                recursionDepth++
                return
            }
        }
    }

    private fun clearApi() {
        api = null
        token = null
    }

    @Provides
    @Singleton
    suspend fun provideSpotifyApi(): SpotifyAppApi {
        if (api == null) {
            buildApi()
        }
        return api!!
    }

    @Provides
    @Singleton
    suspend fun provideSpotifyToken(): Token {
        if (token == null) {
            buildApi()
        }
        return token!!
    }

}
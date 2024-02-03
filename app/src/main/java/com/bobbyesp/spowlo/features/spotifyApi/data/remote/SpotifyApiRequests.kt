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
    private const val CLIENT_ID = BuildConfig.CLIENT_ID
    private const val CLIENT_SECRET = BuildConfig.CLIENT_SECRET
    private var api: SpotifyAppApi? = null
    private var token: Token? = null

    private var recursionDepth = 0
    private const val MAX_RECURSION_DEPTH = 3

    private suspend fun buildApi() {
        try {
            if (isDebug) Log.d(
                "SpotifyApiRequests",
                "Building API with client ID: $CLIENT_ID and client secret: $CLIENT_SECRET"
            )
            token = spotifyAppApi(CLIENT_ID, CLIENT_SECRET).build().token
            api = spotifyAppApi(CLIENT_ID, CLIENT_SECRET, token!!) {
                automaticRefresh = true
            }.build()
        } catch (e: Exception) {
            if (isDebug) Log.e("SpotifyApiRequests", "Error building API", e)
        } catch (e: SpotifyException.BadRequestException) {
            if (token != null && token!!.shouldRefresh() && recursionDepth < MAX_RECURSION_DEPTH) {
                Log.i(
                    "SpotifyApiRequests",
                    "Token expired, refreshing token; recursion depth: $recursionDepth of $MAX_RECURSION_DEPTH"
                )
                clearApi()
                buildApi()
                recursionDepth++
                return
            }
        } catch (e: NullPointerException) {
            if (isDebug) Log.e("SpotifyApiRequests", "Error building API", e)
        }
    }

    private fun clearApi() {
        api = null
        token = null
    }

    @Provides
    @Singleton
    @Throws(IllegalStateException::class)
    suspend fun provideSpotifyApi(): SpotifyAppApi {
        if (api == null) {
            buildApi()
        }
        return api ?: throw IllegalStateException("Spotify API is null")
    }

    @Provides
    @Singleton
    @Throws(IllegalStateException::class)
    suspend fun provideSpotifyToken(): Token {
        if (token == null) {
            buildApi()
        }
        return token ?: throw IllegalStateException("Spotify Token is null")
    }
}
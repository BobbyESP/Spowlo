package com.bobbyesp.spowlo.features.spotifyApi.utils.login

import android.content.Context
import android.util.Log
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyException
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.bobbyesp.spowlo.BuildConfig
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.features.spotifyApi.data.local.login.CredentialsStorer
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.SpotifyPkceLoginImpl
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SpotifyAuthManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SpotifyAuthManager {

    private val credentialsStorer by lazy { CredentialsStorer() }

    private var spotifyClientApi: SpotifyClientApi? = null
    private val credentials = credentialsStorer.provideCredentials(context)
    private val activityWrapper by lazy { ActivityCallsShortener(MainActivity.getActivity()) }

    override fun launchLoginActivity() {
        activityWrapper.execute {
            startSpotifyClientPkceLoginActivity(SpotifyPkceLoginImpl::class.java)
        }
    }

    override fun getSpotifyClientApi(): SpotifyClientApi? {
        if (spotifyClientApi != null) {
            return spotifyClientApi
        }

        //Verify if the user is logged in and return the SpotifyClientApi if it is
        spotifyClientApi = credentials.getSpotifyClientPkceApi { automaticRefresh = true }
        return spotifyClientApi
    }

    override suspend fun isAuthenticated(): Boolean {
        return try {
            val isTokenValid =
                credentials.getSpotifyClientPkceApi()?.isTokenValid()?.isValid ?: false
            val isClientApiInstanceNonNull = spotifyClientApi != null
            if (BuildConfig.DEBUG) Log.i(
                "SearchViewModel",
                "isAuthenticated --> isTokenValid: $isTokenValid, isClientApiValid: $isClientApiInstanceNonNull"
            )
            isTokenValid
        } catch (e: Throwable) {
            Log.e("SpotifyAuthManager", "Error checking if user is authenticated", e)
            false
        }
    }

    override fun shouldRefreshToken(): Boolean {
        return spotifyClientApi?.token?.shouldRefresh() ?: true
    }

    override suspend fun refreshToken(): Boolean {
        return try {
            Log.i("SpotifyAuthManager", "Refreshing token...")
            val api = credentials.getSpotifyClientPkceApi()
                ?: throw SpotifyException.ReAuthenticationNeededException()
            api.refreshToken()
            credentials.spotifyToken = api.token
            true
        } catch (e: SpotifyException.ReAuthenticationNeededException) {
            Log.e("SpotifyAuthManager", "Error refreshing token", e)
            throw e // Throw the exception for being handled by the places where SpotifyAuthManager is used
        }
    }

    override suspend fun createCredentials(): Boolean {
        return try {
            credentialsStorer.createCredentials(context)
            true
        } catch (e: Throwable) {
            Log.e("SpotifyAuthManager", "Error creating credentials", e)
            false
        }
    }
    override fun credentialsFileExists(): Boolean {
        TODO()
    }

    override fun deleteCredentials(): Boolean {
        return try {
            credentials.clear()
            true
        } catch (e: Throwable) {
            false
        }
    }
}
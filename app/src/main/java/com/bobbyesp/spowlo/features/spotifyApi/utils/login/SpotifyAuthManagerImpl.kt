package com.bobbyesp.spowlo.features.spotifyApi.utils.login

import android.content.Context
import android.util.Log
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyException
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.bobbyesp.spowlo.BuildConfig
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.CredentialsStorer
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.SpotifyPkceLoginImpl
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SpotifyAuthManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SpotifyAuthManager {

    private var spotifyClientApi: SpotifyClientApi? = null
    private val credentials = CredentialsStorer().provideCredentials(context)
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
        spotifyClientApi = credentials.getSpotifyClientPkceApi()
        return spotifyClientApi
    }

    override suspend fun isAuthenticated(): Boolean {
        val isTokenValid = credentials.getSpotifyClientPkceApi()?.isTokenValid()?.isValid ?: false
        val isClientApiValid = spotifyClientApi != null
        if(BuildConfig.DEBUG) Log.i("SearchViewModel", "isAuthenticated: isTokenValid: $isTokenValid, isClientApiValid: $isClientApiValid")
        return isTokenValid && isClientApiValid
    }

    override suspend fun refreshToken(): Boolean {
        return try {
            val api = credentials.getSpotifyClientPkceApi() ?: throw SpotifyException.ReAuthenticationNeededException()
            api.refreshToken()
            credentials.spotifyToken = api.token
            true
        } catch (e: SpotifyException.ReAuthenticationNeededException) {
            throw e // Throw the exception for being handled by the places where SpotifyAuthManager is used
        }
    }

    override fun deleteCredentials(): Boolean {
        return try {
            credentials.clear()
            true
        } catch (e: Exception) {
            false
        }
    }
}
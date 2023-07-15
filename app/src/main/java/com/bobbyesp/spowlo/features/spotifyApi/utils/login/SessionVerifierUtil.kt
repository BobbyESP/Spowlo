package com.bobbyesp.spowlo.features.spotifyApi.utils.login

import android.app.Activity
import android.content.Context
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyException
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.CredentialsStorer
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.SpotifyPkceLoginImpl
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.pkceClassBackTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Checks if the Spotify API is valid (using PKCE authentication), and if not, re-authenticates the user.
 * @param activity The activity to use for the authentication
 * @param alreadyTriedToReauthenticate Whether or not the function has already tried to re-authenticate the user
 * @param block The block to run if the API is valid
 * @return The result of the block, or null if the API is invalid and the user has already been re-authenticated
 */
suspend fun <T> checkSpotifyApiIsValid(
    activity: Activity,
    applicationContext: Context,
    alreadyTriedToReauthenticate: Boolean = false,
    block: suspend (api: SpotifyClientApi) -> T
): T? {
    val classToGoBackTo: Class<out Activity> = activity::class.java

    try {
        val apiCredentials = withContext(Dispatchers.Main) {
            CredentialsStorer().provideCredentials(applicationContext)
        }
        val api = apiCredentials.getSpotifyClientPkceApi()
            ?: throw SpotifyException.ReAuthenticationNeededException()

        return block(api)
    } catch (e: SpotifyException) {
        e.printStackTrace()
        val apiCredentials = withContext(Dispatchers.Main) {
            CredentialsStorer().provideCredentials(applicationContext)
        }

        if (!alreadyTriedToReauthenticate) {
            val api = apiCredentials.getSpotifyClientPkceApi()
                ?: throw SpotifyException.ReAuthenticationNeededException()
            return try {
                api.refreshToken()
                apiCredentials.spotifyToken = api.token

                block(api)
            } catch (e: SpotifyException.ReAuthenticationNeededException) {
                e.printStackTrace()

                checkSpotifyApiIsValid(
                    activity,
                    applicationContext,
                    true,
                    block
                )
            }
        } else {
            pkceClassBackTo = classToGoBackTo
            activity.startSpotifyClientPkceLoginActivity(SpotifyPkceLoginImpl::class.java)
            return null
        }
    }
}

fun checkIfLoggedIn(applicationContext: Context): Boolean {
    val apiCredentials = CredentialsStorer().provideCredentials(applicationContext)
    return try {
        apiCredentials.getSpotifyClientPkceApi()
            ?: throw SpotifyException.ReAuthenticationNeededException()
        true
    } catch (e: SpotifyException.ReAuthenticationNeededException) {
        false
    }
}

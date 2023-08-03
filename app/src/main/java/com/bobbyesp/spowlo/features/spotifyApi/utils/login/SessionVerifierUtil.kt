package com.bobbyesp.spowlo.features.spotifyApi.utils.login

import android.app.Activity
import android.content.Context
import android.util.Log
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyException
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.CredentialsStorer
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.SpotifyPkceLoginImpl
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.pkceClassBackTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

/**
 * Checks if the Spotify API is valid (using PKCE authentication), and if not, re-authenticates the user.
 * @param activity The activity to use for the authentication
 * @param alreadyTriedToReauthenticate Whether or not the function has already tried to re-authenticate the user
 * @param onNetworkError If the API calls fails because of a network error, this will be triggered
 * @param block The block to run if the API is valid
 * @return The result of the block, or null if the API is invalid and the user has already been re-authenticated
 */
suspend fun <T> checkSpotifyApiIsValid(
    activity: Activity = MainActivity.getActivity(),
    applicationContext: Context,
    alreadyTriedToReauthenticate: Boolean = false,
    onNetworkError: (error: Exception) -> Unit = {},
    block: suspend (api: SpotifyClientApi) -> T
): T? {
    val classToGoBackTo: Class<out Activity> = activity::class.java

    try {
        val apiCredentials = withContext(Dispatchers.Main) {
            CredentialsStorer().provideCredentials(applicationContext)
        }
        val api = apiCredentials.getSpotifyClientPkceApi()
            ?: throw SpotifyException.ReAuthenticationNeededException() //CAUTION HERE

        return block(api)
    } catch (e: SpotifyException) {
        e.printStackTrace()
        val apiCredentials = withContext(Dispatchers.Main) {
            CredentialsStorer().provideCredentials(applicationContext)
        }

        if (!alreadyTriedToReauthenticate) {
            Log.i("SessionVerifierUtil", "checkSpotifyApiIsValid: Trying to refresh user token")
            return try {
                val api = apiCredentials.getSpotifyClientPkceApi()
                    ?: throw SpotifyException.ReAuthenticationNeededException()
                api.refreshToken()
                apiCredentials.spotifyToken = api.token

                block(api)
            } catch (e: SpotifyException.ReAuthenticationNeededException) {
                e.printStackTrace()

                checkSpotifyApiIsValid(
                    activity,
                    applicationContext,
                    true,
                    onNetworkError,
                    block
                )
            }
        } else {
            pkceClassBackTo = classToGoBackTo
            runBlocking {
                activity.startSpotifyClientPkceLoginActivity(SpotifyPkceLoginImpl::class.java)
            }
            return null
        }
    } catch (e: UnknownHostException) {
        e.printStackTrace()
        onNetworkError(e)
        return null
    }
}
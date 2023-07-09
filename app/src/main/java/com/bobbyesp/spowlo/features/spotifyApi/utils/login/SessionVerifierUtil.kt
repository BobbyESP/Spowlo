package com.bobbyesp.spowlo.features.spotifyApi.utils.login

import android.app.Activity
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyException
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.CredentialsStorer
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.SpotifyPkceLoginImpl
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.pkceClassBackTo
import kotlinx.coroutines.runBlocking

/**
 * Checks if the Spotify API is valid (using PKCE authentication), and if not, re-authenticates the user.
 * @param activity The activity to use for the authentication
 * @param alreadyTriedToReauthenticate Whether or not the function has already tried to re-authenticate the user
 * @param block The block to run if the API is valid
 * @return The result of the block, or null if the API is invalid and the user has already been re-authenticated
 */
suspend fun <T> checkSpotifyApiIsValid(
    activity: Activity,
    alreadyTriedToReauthenticate: Boolean = false,
    block: suspend (api: SpotifyClientApi) -> T
): T? {
    val classToGoBackTo: Class<out Activity> = activity::class.java

    return runBlocking {
        try {
            val apiCredentials = CredentialsStorer(activity.applicationContext).provideCredentials()
            val api = apiCredentials.getSpotifyClientPkceApi() ?: throw SpotifyException.ReAuthenticationNeededException()

            block(api)
        } catch (e: SpotifyException) {
            e.printStackTrace()
            val apiCredentials = CredentialsStorer(activity.applicationContext).provideCredentials()
            if (!alreadyTriedToReauthenticate) {
                val api = apiCredentials.getSpotifyClientPkceApi()!!
                try {
                    api.refreshToken()
                    apiCredentials.spotifyToken = api.token
                    block(api)
                } catch (e: SpotifyException.ReAuthenticationNeededException) {
                    e.printStackTrace()
                    return@runBlocking checkSpotifyApiIsValid(
                        activity,
                        true,
                        block
                    )
                }
            } else {
                pkceClassBackTo = classToGoBackTo
                activity.startSpotifyClientPkceLoginActivity(SpotifyPkceLoginImpl::class.java)
                null
            }
        }
    }
}

fun checkIfLoggedIn(): Boolean {
    val apiCredentials = CredentialsStorer(MainActivity.getActivity().applicationContext).provideCredentials()
    return apiCredentials.getSpotifyClientPkceApi() != null
}
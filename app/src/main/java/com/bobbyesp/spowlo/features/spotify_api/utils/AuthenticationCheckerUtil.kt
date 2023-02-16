package com.bobbyesp.spowlo.features.spotify_api.utils

import androidx.navigation.NavController
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyException
import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.features.spotify_api.auth.SpotifyImplicitLoginActivityImpl
import com.bobbyesp.spowlo.features.spotify_api.auth.SpotifyPkceLoginActivityImpl
import com.bobbyesp.spowlo.features.spotify_api.auth.pkceClassBackTo
import com.bobbyesp.spowlo.features.spotify_api.data.auth.AuthModel
import com.bobbyesp.spowlo.ui.common.Route
import kotlinx.coroutines.runBlocking
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.adamratzman.spotify.auth.implicit.startSpotifyImplicitLoginActivity

fun isSpotifyAuthenticated(): Boolean {
    val model = AuthModel
    return model.credentialStore.spotifyAccessToken != null
}

fun <T> NavController.guardValidSpotifyApi(
    pageBackTo: Route,
    alreadyTriedToReauthenticate: Boolean = false,
    block: suspend (api: SpotifyClientApi) -> T
): T? {
    return runBlocking {
        try {
            val token = AuthModel.credentialStore.spotifyToken
                ?: throw SpotifyException.ReAuthenticationNeededException()
            val usesPkceAuth = token.refreshToken != null
            val api = (if (usesPkceAuth) AuthModel.credentialStore.getSpotifyClientPkceApi()
            else AuthModel.credentialStore.getSpotifyImplicitGrantApi())
                ?: throw SpotifyException.ReAuthenticationNeededException()

            block(api)
        } catch (e: SpotifyException) {
            e.printStackTrace()
            val usesPkceAuth = AuthModel.credentialStore.spotifyToken?.refreshToken != null
            if (usesPkceAuth) {
                val api = AuthModel.credentialStore.getSpotifyClientPkceApi()!!
                if (!alreadyTriedToReauthenticate) {
                    try {
                        api.refreshToken()
                        AuthModel.credentialStore.spotifyToken = api.token
                        block(api)
                    } catch (e: SpotifyException.ReAuthenticationNeededException) {
                        e.printStackTrace()
                        return@runBlocking guardValidSpotifyApi(
                            pageBackTo = pageBackTo,
                            alreadyTriedToReauthenticate = true,
                            block = block
                        )
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                        return@runBlocking guardValidSpotifyApi(
                            pageBackTo = pageBackTo,
                            alreadyTriedToReauthenticate = true,
                            block = block
                        )
                    }
                } else {
                    pkceClassBackTo = MainActivity::class.java
                    //Create an intent to start the Spotify login activity
                    //startSpotifyClientPkceLoginActivity(SpotifyPkceLoginActivityImpl::class.java)
                    null
                }
            } else {
                SpotifyDefaultCredentialStore.activityBackOnImplicitAuth = MainActivity::class.java
                //startSpotifyImplicitLoginActivity(SpotifyImplicitLoginActivityImpl::class.java)
                null
            }
        }
    }
}
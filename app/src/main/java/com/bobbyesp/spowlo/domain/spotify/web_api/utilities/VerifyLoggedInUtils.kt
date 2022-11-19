package com.bobbyesp.spowlo.domain.spotify.web_api.utilities

import android.app.Activity
import android.util.Log
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyException
import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import com.adamratzman.spotify.auth.implicit.startSpotifyImplicitLoginActivity
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.bobbyesp.spowlo.data.auth.AuthModel
import com.bobbyesp.spowlo.domain.spotify.web_api.auth.SpotifyImplicitLoginActivityImpl
import com.bobbyesp.spowlo.domain.spotify.web_api.auth.SpotifyPkceLoginActivityImpl
import com.bobbyesp.spowlo.domain.spotify.web_api.auth.pkceClassBackTo
import kotlinx.coroutines.runBlocking

fun <T> Activity.guardValidSpotifyApi(
    classBackTo: Class<out Activity>,
    alreadyTriedToReauthenticate: Boolean = false,
    block: suspend (api: SpotifyClientApi) -> T
): T? {
    return runBlocking {
        try {
            val api = AuthModel.credentialStore.getSpotifyClientPkceApi() ?: throw SpotifyException.ReAuthenticationNeededException()
            block(api)
        } catch (e: SpotifyException) {
            e.printStackTrace()
                val api = AuthModel.credentialStore.getSpotifyClientPkceApi()!!
                if (!alreadyTriedToReauthenticate) {
                    try {
                        api.refreshToken()
                        AuthModel.credentialStore.spotifyToken = api.token
                        block(api)
                    } catch (e: SpotifyException.ReAuthenticationNeededException) {
                        e.printStackTrace()
                        return@runBlocking guardValidSpotifyApi(
                            classBackTo = classBackTo,
                            alreadyTriedToReauthenticate = true,
                            block = block
                        )
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                        return@runBlocking guardValidSpotifyApi(
                            classBackTo = classBackTo,
                            alreadyTriedToReauthenticate = true,
                            block = block
                        )
                    }
                } else {
                    pkceClassBackTo = classBackTo
                    startSpotifyClientPkceLoginActivity(SpotifyPkceLoginActivityImpl::class.java)
                    null
                }
            }
        }
    }
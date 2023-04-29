package com.bobbyesp.appmodules.core.api

import com.bobbyesp.appmodules.core.SpotifySessionManager
import com.spotify.clienttoken.http.v0.ClientToken
import xyz.gianlu.librespot.dealer.ApiClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClientTokenHandler @Inject constructor(
    private val spSessionManager: SpotifySessionManager
) {
    private val apiMethod = ApiClient::class.java.getDeclaredMethod("clientToken").also { it.isAccessible = true }
    private var sessionToken: String = ""

    fun requestToken() = sessionToken.ifEmpty {
        (apiMethod.invoke(spSessionManager.session.api()) as ClientToken.ClientTokenResponse).grantedToken.token.also {
            sessionToken = it
        }
    }
}
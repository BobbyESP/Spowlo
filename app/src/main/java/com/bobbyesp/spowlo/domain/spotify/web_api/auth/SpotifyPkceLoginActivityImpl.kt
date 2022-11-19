package com.bobbyesp.spowlo.domain.spotify.web_api.auth

import android.app.Activity
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.auth.pkce.AbstractSpotifyPkceLoginActivity
import com.bobbyesp.spowlo.BuildConfig
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.Spowlo
import com.bobbyesp.spowlo.presentation.MainActivity
import com.bobbyesp.spowlo.util.Utils.makeToast

internal var pkceClassBackTo: Class<out Activity>? = null

class SpotifyPkceLoginActivityImpl: AbstractSpotifyPkceLoginActivity() {
    override val clientId = R.string.SPOTIFY_CLIENT_ID.toString()
    override val redirectUri = R.string.SPOTIFY_REDIRECT_URI_PKCE.toString()
    override val scopes = SpotifyScope.values().toList()

    override fun onFailure(exception: Exception) {
        exception.printStackTrace()
        pkceClassBackTo = null
        makeToast("Auth failed: ${exception.message}")
    }

    override fun onSuccess(api: SpotifyClientApi) {
        val model = (application as Spowlo).model
        model.credentialStore.setSpotifyApi(api)
        val classBackTo = pkceClassBackTo ?: MainActivity::class.java
        pkceClassBackTo = null
    }
}
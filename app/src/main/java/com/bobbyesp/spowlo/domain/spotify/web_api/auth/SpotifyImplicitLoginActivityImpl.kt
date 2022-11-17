package com.bobbyesp.spowlo.domain.spotify.web_api.auth

import android.content.Intent
import com.adamratzman.spotify.SpotifyImplicitGrantApi
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.auth.implicit.AbstractSpotifyAppImplicitLoginActivity
import com.bobbyesp.spowlo.BuildConfig
import com.bobbyesp.spowlo.Spowlo
import com.bobbyesp.spowlo.presentation.MainActivity
import com.bobbyesp.spowlo.util.Utils.makeToast

class SpotifyImplicitLoginActivityImpl: AbstractSpotifyAppImplicitLoginActivity() {
    override val state: Int = 1337
    override val clientId: String = BuildConfig.SPOTIFY_CLIENT_ID
    override val redirectUri: String = BuildConfig.SPOTIFY_REDIRECT_URI_AUTH
    override val useDefaultRedirectHandler: Boolean = false
    override fun getRequestingScopes(): List<SpotifyScope> = SpotifyScope.values().toList()

    override fun onFailure(errorMessage: String) {
        makeToast("Auth failed: $errorMessage")
    }

    override fun onSuccess(spotifyApi: SpotifyImplicitGrantApi) {
        val model = (application as Spowlo).model
        model.credentialStore.setSpotifyApi(spotifyApi)
        startActivity(Intent(this, MainActivity::class.java))
    }

}
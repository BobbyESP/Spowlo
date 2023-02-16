package com.bobbyesp.spowlo.features.spotify_api.data.auth

import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.BuildConfig

object AuthModel {
        val credentialStore by lazy {
            SpotifyDefaultCredentialStore(
                clientId = BuildConfig.SPOTIFY_CLIENT_ID,
                redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE,
                applicationContext = App.context
            )
        }

}
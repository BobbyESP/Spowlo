package com.bobbyesp.spowlo.data.auth

import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import com.bobbyesp.spowlo.BuildConfig
import com.bobbyesp.spowlo.Spowlo

object AuthModel {
    val credentialStore by lazy {
        SpotifyDefaultCredentialStore(
            clientId = BuildConfig.SPOTIFY_CLIENT_ID,
            redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE,
            applicationContext = Spowlo.context
        )
    }
}
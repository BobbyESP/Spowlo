package com.bobbyesp.spowlo.features.spotifyApi.data.remote.login

import android.content.Context
import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import com.bobbyesp.spowlo.BuildConfig

class CredentialsStorer {
    fun provideCredentials(context: Context): SpotifyDefaultCredentialStore {
        val credentialStore by lazy {
            SpotifyDefaultCredentialStore(
                clientId = BuildConfig.CLIENT_ID,
                redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE,
                applicationContext = context
            )
        }
        return credentialStore
    }
}
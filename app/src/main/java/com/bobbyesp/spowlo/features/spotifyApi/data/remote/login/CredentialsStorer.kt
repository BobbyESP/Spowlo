package com.bobbyesp.spowlo.features.spotifyApi.data.remote.login

import android.content.Context
import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import com.bobbyesp.spowlo.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CredentialsStorer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun provideCredentials(): SpotifyDefaultCredentialStore {
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
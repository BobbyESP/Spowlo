package com.bobbyesp.spowlo.features.spotifyApi.data.remote.login

import android.content.Context
import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import com.bobbyesp.spowlo.BuildConfig

class CredentialsStorer {
    fun provideCredentials(context: Context): SpotifyDefaultCredentialStore {
        val credentialStore by lazy { //Lazy so that it is only created when needed. When we call another time the same function, it will return the same instance, not a new one.
            SpotifyDefaultCredentialStore(
                clientId = BuildConfig.CLIENT_ID,
                redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE,
                applicationContext = context
            )
        }
        return credentialStore
    }

    fun deleteCredentials(context: Context) {
        val credentialStore = provideCredentials(context)
        credentialStore.clear()
    }
}
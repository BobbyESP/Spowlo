package com.bobbyesp.spowlo.features.spotifyApi.data.local.login

import android.content.Context
import android.util.Log
import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import com.bobbyesp.spowlo.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

object CredentialsStorer {
    lateinit var credentials: SpotifyDefaultCredentialStore

    init {
        Log.i("CredentialsStorer", "CredentialsStorer initialized")
    }

    suspend fun createCredentials(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                async { provideCredentials(context) }.await()
                true
            } catch (e: Exception) {
                Log.e("CredentialsStorer", "Error creating credentials", e)
                false
            }
        }
    }

    fun provideCredentials(context: Context): SpotifyDefaultCredentialStore {
        if (!::credentials.isInitialized) {
            credentials = SpotifyDefaultCredentialStore(
                clientId = BuildConfig.CLIENT_ID,
                redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE,
                applicationContext = context
            )
        }
        return credentials
    }

}
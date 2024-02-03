package com.bobbyesp.spowlo.features.spotifyApi.data.local.login

import android.content.Context
import android.util.Log
import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import com.bobbyesp.spowlo.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class CredentialsStorer {
    suspend fun createCredentials(context: Context): Boolean {
        return try {
            val credentials = withContext(Dispatchers.IO) {
                async {
                    SpotifyDefaultCredentialStore(
                        clientId = BuildConfig.CLIENT_ID,
                        redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE,
                        applicationContext = context
                    ).encryptedPreferences
                }
            }
            credentials.await()
            true
        } catch (e: Throwable) {
            Log.e("CredentialsStorer", "Error creating credentials", e)
            false
        }
    }

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
}
package com.bobbyesp.spowlo.features.spotify.auth

import android.content.Context
import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import com.bobbyesp.spowlo.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

/**
 * Singleton object that manages the creation and retrieval of SpotifyDefaultCredentialStore.
 */
object CredentialsStorer {
    // Deferred instance of SpotifyDefaultCredentialStore. It's nullable and initially set to null.
    private var credentialStoreDeferred: Deferred<SpotifyDefaultCredentialStore>? = null

    /**
     * Initializes the SpotifyDefaultCredentialStore instance if it's not already initialized.
     * This function uses the IO dispatcher to run the initialization in a separate thread.
     * @param context The context needed to create the SpotifyDefaultCredentialStore instance.
     */
    fun initializeCredentials(context: Context) {
        if (credentialStoreDeferred == null) {
            credentialStoreDeferred = CoroutineScope(Dispatchers.IO).async {
                SpotifyDefaultCredentialStore(
                    applicationContext = context.applicationContext,
                    clientId = BuildConfig.CLIENT_ID,
                    redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE,
                )
            }
        }
    }

    /**
     * Retrieves the SpotifyDefaultCredentialStore instance.
     * This function is a suspending function and should be called from a coroutine or another suspending function.
     * @return The SpotifyDefaultCredentialStore instance.
     * @throws IllegalStateException If the SpotifyDefaultCredentialStore instance is not initialized before calling this function.
     */
    suspend fun getCredentials(): SpotifyDefaultCredentialStore {
        return credentialStoreDeferred?.await() ?: throw IllegalStateException("Credentials not initialized")
    }
}
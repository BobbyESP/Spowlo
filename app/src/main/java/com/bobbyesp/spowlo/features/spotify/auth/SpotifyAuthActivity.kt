package com.bobbyesp.spowlo.features.spotify.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.adamratzman.spotify.SpotifyApiOptions
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyUserAuthorization
import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import com.adamratzman.spotify.auth.pkce.isSpotifyPkceAuthIntent
import com.adamratzman.spotify.spotifyClientPkceApi
import com.bobbyesp.spowlo.BuildConfig
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class SpotifyAuthActivity : ComponentActivity() {
    private val clientId: String = BuildConfig.CLIENT_ID
    private val redirectUri: String = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE
    private lateinit var pkceCodeVerifier: String
    private lateinit var state: String
    private lateinit var authorizationUrl: String
    private lateinit var spCredentialsStorer: SpotifyDefaultCredentialStore

    open val options: ((SpotifyApiOptions).() -> Unit)? = null

    /**
     * Custom logic to invoke when loading begins ([isLoading] is true) or ends ([isLoading] is false).
     * This is useful for showing/hiding loading indicators.
     */
    open fun setLoadingState(isLoading: Boolean): () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            spCredentialsStorer = CredentialsStorer.getCredentials()
        }
        val authenticationVariables = getAuthenticationVariables()

        pkceCodeVerifier = authenticationVariables.codeVerifier
        state = authenticationVariables.state
        authorizationUrl = authenticationVariables.authorizationUrl

        spCredentialsStorer.currentSpotifyPkceCodeVerifier = pkceCodeVerifier
        launchSpotifyAuth(this, authorizationUrl)
    }

    /**
     * The callback that will be executed after successful PKCE authorization.
     *
     * @param api The built [SpotifyClientApi] corresponding to the retrieved token from PKCE auth.
     */
    abstract fun onSuccess(api: SpotifyClientApi)

    /**
     * The callback that will be executed after unsuccessful PKCE authorization.
     *
     * @param exception The root cause of the auth failure.
     */
    abstract fun onFailure(exception: Exception)

    /**
     * Retrieves the authentication variables from a file or generates new ones if they don't exist or are expired.
     *
     * @return AuthenticationVariables The authentication variables.
     */
    private fun getAuthenticationVariables(): AuthenticationVariables {
        val jsonAuthorizationVariables = SpotifyAuthenticationHelpers.readVariablesFromFile(this)

        return if (jsonAuthorizationVariables != null && !jsonAuthorizationVariables.isExpired) {
            jsonAuthorizationVariables
        } else {
            val pkceCodeVerifier = generatePkceCodeVerifier()
            val codeChallenge = createCodeChallenge(pkceCodeVerifier)
            val state = generateStateString()
            val authorizationUrl = generateAuthUrl(codeChallenge, state)
            val variables = AuthenticationVariables(
                codeVerifier = pkceCodeVerifier,
                codeChallenge = codeChallenge,
                state = state,
                authorizationUrl = authorizationUrl
            )
            SpotifyAuthenticationHelpers.writeVariablesToFile(this, variables)
            variables
        }
    }

    /**
     * Handles new intents. If the intent has data, it sets the intent as the current intent.
     *
     * @param intent The new intent.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { _ ->
            setIntent(intent)
        }
    }

    /**
     * Handles resuming of the activity. If the intent is a Spotify PKCE auth intent, it handles the auth redirection.
     */
    override fun onResume() {
        super.onResume()
        intent?.let { currentIntent ->
            if (currentIntent.isSpotifyPkceAuthIntent(redirectUri)) {
                lifecycleScope.launch(Dispatchers.IO) {
                    handleAuthRedirection(AuthorizationResponse.fromUri(currentIntent.data))
                }
                // Clear the intent after processing it
                intent = null
            }
        }
    }

    /**
     * Handles the auth redirection. If the response type is CODE, it attempts to exchange the auth code for an access token.
     * If successful, it updates the credentials and calls the onSuccess callback. If unsuccessful, it calls the onFailure callback.
     * If the response type is not CODE, it calls the onFailure callback with an appropriate error message.
     *
     * @param response The authorization response.
     */
    private suspend fun handleAuthRedirection(response: AuthorizationResponse) {
        if (response.type == AuthorizationResponse.Type.CODE) {
            val authCode = response.code
            if (authCode.isNullOrBlank()) {
                onFailure(IllegalStateException("Received auth code is empty!"))
                finish()
            } else {
                try {
                    Log.i(TAG, "Attempting to exchange auth code for access token.")
                    setLoadingState(true)()
                    Log.i(TAG, pkceCodeVerifier)
                    val api = spotifyClientPkceApi(
                        clientId = clientId,
                        redirectUri = redirectUri,
                        authorization = SpotifyUserAuthorization(
                            authorizationCode = authCode, pkceCodeVerifier = pkceCodeVerifier
                        ),
                        block = options ?: {}
                    ).build()

                    if (api.token.accessToken.isNotBlank()) {
                        //We modify the Credentials storer. DO NOT MODIFY IT AFTER ON SUCCESS
                        val credentials = CredentialsStorer.getCredentials()
                        credentials.spotifyToken = api.token
                        credentials.setSpotifyApi(api)
                        Log.i(TAG, "Successfully exchanged auth code for access token.")
                        onSuccess(api)
                        SpotifyAuthenticationHelpers.deleteVariablesFile(this@SpotifyAuthActivity)
                    } else {
                        onFailure(IllegalStateException("Received an empty access token!"))
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed exchanging auth code for access token.", e)
                    onFailure(e)
                }
                Log.i(TAG, "Got an auth code of ${authCode.substring(0, 10)}...")
                finish()
            }
        } else {
            when (response.type) {
                AuthorizationResponse.Type.TOKEN, AuthorizationResponse.Type.ERROR, AuthorizationResponse.Type.EMPTY, AuthorizationResponse.Type.UNKNOWN -> {
                    Log.i(TAG, "Got an error response of ${response.error}")
                    onFailure(IllegalStateException("Received an error response!"))
                }

                else -> {
                    onFailure(IllegalStateException("Got an unknown response type of ${response.type}"))
                }
            }
        }
        finish()
    }

    companion object {
        private const val TAG = "SpotifyAuthActivity"
    }
}

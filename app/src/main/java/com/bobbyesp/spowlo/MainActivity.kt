package com.bobbyesp.spowlo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.adamratzman.spotify.SpotifyUserAuthorization
import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import com.adamratzman.spotify.auth.pkce.isSpotifyPkceAuthIntent
import com.adamratzman.spotify.spotifyClientPkceApi
import com.bobbyesp.spowlo.features.spotify.auth.CredentialsStorer
import com.bobbyesp.spowlo.presentation.Navigator
import com.bobbyesp.spowlo.presentation.common.AppLocalSettingsProvider
import com.bobbyesp.spowlo.presentation.common.LocalDarkTheme
import com.bobbyesp.spowlo.presentation.pages.spotify.auth.AuthenticationVariables
import com.bobbyesp.spowlo.presentation.pages.spotify.auth.SpotifyAuthenticationHelpers
import com.bobbyesp.spowlo.presentation.pages.spotify.auth.createCodeChallenge
import com.bobbyesp.spowlo.presentation.pages.spotify.auth.generateAuthUrl
import com.bobbyesp.spowlo.presentation.pages.spotify.auth.generatePkceCodeVerifier
import com.bobbyesp.spowlo.presentation.pages.spotify.auth.generateStateString
import com.bobbyesp.spowlo.presentation.theme.SpowloTheme
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val clientId: String = BuildConfig.CLIENT_ID
    private val redirectUri: String = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE
    private lateinit var pkceCodeVerifier: String
    private lateinit var state: String
    lateinit var authorizationUrl: String
    private lateinit var spotifyCredentials: SpotifyDefaultCredentialStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            spotifyCredentials = CredentialsStorer.getCredentials()
        }

        val authenticationVariables = getAuthenticationVariables()

        pkceCodeVerifier = authenticationVariables.codeVerifier
        state = authenticationVariables.state
        authorizationUrl = authenticationVariables.authorizationUrl

        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
            view.setPadding(0, 0, 0, 0)
            insets
        }
        activity = this
        enableEdgeToEdge()
        setContent {
            val windowWidthClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
            AppLocalSettingsProvider(windowWidthClass) {
                SpowloTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                ) {
                    Navigator()
                }
            }
        }
    }

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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { _ ->
            setIntent(intent)
        }
    }

    /**
     * This function is called when the activity has been paused or stopped, and is now resuming.
     * It checks if the current intent is a Spotify PKCE Auth Intent. If it is, then is launched
     * a coroutine to handle the authorization redirection.
     */
    override fun onResume() {
        super.onResume()
        // Check if the current intent is a Spotify PKCE Auth Intent
        if (intent?.isSpotifyPkceAuthIntent(redirectUri) == true) {
            // Launch a coroutine in the IO dispatcher to handle the authorization redirection
            lifecycleScope.launch(Dispatchers.IO) {
                handleAuthRedirection(AuthorizationResponse.fromUri(intent.data))
            }
        }
    }

    private suspend fun handleAuthRedirection(response: AuthorizationResponse) {
        if (response.type == AuthorizationResponse.Type.CODE) {
            val authCode = response.code
            if (authCode.isNullOrBlank()) {
                throw IllegalStateException("Received auth code is empty!")
            } else {
                try {
                    Log.i(TAG, "handleAuthRedirection: Code verifier: $pkceCodeVerifier")
                    Log.i(
                        TAG,
                        "handleAuthRedirection: Attempting to exchange auth code for access token." +
                                " (Build PKCE Client API)"
                    )
                    val api = spotifyClientPkceApi(
                        clientId = clientId,
                        redirectUri = redirectUri,
                        authorization = SpotifyUserAuthorization(
                            authorizationCode = authCode,
                            pkceCodeVerifier = pkceCodeVerifier
                        )
                    ) {
                        //API Options
                    }.build()

                    Log.i(TAG, "handleAuthRedirection: Successfully built PKCE client API.")
                    if (api.token.accessToken.isNotBlank()) {
                        spotifyCredentials.spotifyToken = api.token
                        spotifyCredentials.setSpotifyApi(api)
                        Log.i(
                            TAG,
                            "handleAuthRedirection: Successfully exchanged auth code for access token."
                        )
                        SpotifyAuthenticationHelpers.deleteVariablesFile(this)
                    } else {
                        throw IllegalStateException("Received an empty access token!")
                    }

                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "handleAuthRedirection: Failed exchanging auth code for access token.",
                        e
                    )
                    throw e
                }
                Log.i(TAG, "handleAuthRedirection: Got an auth code of $authCode")
            }
        } else {
            when (response.type) {
                AuthorizationResponse.Type.TOKEN,
                AuthorizationResponse.Type.ERROR,
                AuthorizationResponse.Type.EMPTY,
                AuthorizationResponse.Type.UNKNOWN -> {
                    Log.i(TAG, "handleAuthRedirection: Got an error response of ${response.error}")
                    throw IllegalStateException("Received an error response!")
                }

                else -> {
                    Log.i(
                        TAG,
                        "handleAuthRedirection: Got an unknown response type of ${response.type}"
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private lateinit var activity: MainActivity
        fun getActivity(): MainActivity {
            return activity
        }
    }
}
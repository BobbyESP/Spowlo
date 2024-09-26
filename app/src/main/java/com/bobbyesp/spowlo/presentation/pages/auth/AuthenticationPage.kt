package com.bobbyesp.spowlo.presentation.pages.auth

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bobbyesp.spowlo.features.spotify.auth.SpotifyAuthActivityImpl
import com.bobbyesp.spowlo.features.spotify.auth.SpotifyAuthState

@Composable
fun AuthenticationPage(
    authState: State<SpotifyAuthState>,
    onLogout: () -> Unit
) {
    AuthenticationPageContent(authState, onLogout = onLogout)
}

@Composable
private fun AuthenticationPageContent(
    authenticationState: State<SpotifyAuthState>,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val authState = authenticationState.value

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when(authState) {
            is SpotifyAuthState.NotAuthenticated -> {
                Button(onClick = {
                    context.startActivity(Intent(context, SpotifyAuthActivityImpl::class.java))
                }) {
                    Text("Log in with Spotify")
                }
            }
            is SpotifyAuthState.LoggingIn -> {
                if(authState.isLoading) {
                    CircularProgressIndicator()
                    Text("Logging in...")
                } else {
                    Text("Finished loading")
                }
            }
            is SpotifyAuthState.Authenticated -> {
                Column {
                    Text("You are logged in.")

                    Button(
                        onClick = {
                            onLogout()
                        }
                    ) {
                        Text("Log out")
                    }
                }
            }
            is SpotifyAuthState.Error -> {
                Text("An error occurred: ${authState.message}")
            }
        }
    }
}
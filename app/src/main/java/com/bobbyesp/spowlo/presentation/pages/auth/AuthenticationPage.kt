package com.bobbyesp.spowlo.presentation.pages.auth

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.features.spotify.auth.SpotifyAuthActivityImpl
import com.bobbyesp.spowlo.features.spotify.auth.SpotifyAuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AuthenticationPage(
    authManagerViewModel: SpotifyAuthManagerViewModel
) {
    val authState = authManagerViewModel.authManagerViewState.collectAsStateWithLifecycle().value
    val scope = rememberCoroutineScope()

    AuthenticationPageContent(authState, onLogout = {
        scope.launch(Dispatchers.IO) {
            authManagerViewModel.logout()
        }
    })
}

@Composable
private fun AuthenticationPageContent(
    authenticationState: SpotifyAuthState,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when(authenticationState) {
            is SpotifyAuthState.NotAuthenticated -> {
                Button(onClick = {
                    context.startActivity(Intent(context, SpotifyAuthActivityImpl::class.java))
                }) {
                    Text("Log in with Spotify")
                }
            }
            is SpotifyAuthState.LoggingIn -> {
                if(authenticationState.isLoading) {
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
                Text("An error occurred: ${authenticationState.message}")
            }
        }
    }
}
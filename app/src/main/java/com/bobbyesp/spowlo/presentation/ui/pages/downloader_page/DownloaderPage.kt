package com.bobbyesp.spowlo.presentation.ui.pages.downloader_page

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adamratzman.spotify.auth.implicit.startSpotifyImplicitLoginActivity
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.bobbyesp.spowlo.data.auth.AuthModel
import com.bobbyesp.spowlo.domain.spotify.web_api.auth.SpotifyImplicitLoginActivityImpl
import com.bobbyesp.spowlo.domain.spotify.web_api.auth.SpotifyPkceLoginActivityImpl
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(
    ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class
)
@Composable
fun DownloaderPage(
    navController: NavController,
    downloadViewModel: DownloaderViewModel = hiltViewModel(),
    activity: Activity? = null
) {
    lateinit var model: AuthModel
    val viewState = downloadViewModel.stateFlow.collectAsState()

    with(viewState.value){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ){
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                color = MaterialTheme.colorScheme.background
            ){
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Button(onClick = {
                        downloadViewModel.spotifyImplicitLogin(activity)
                    }) {
                        Text("Connect to Spotify (spotify-auth integration, Implicit Grant)")
                    }
                    Button(onClick = {
                        downloadViewModel.spotifyPkceLogin(activity)
                    }) {
                        Text("Connect to Spotify (spotify-web-api-kotlin integration, PKCE auth)")
                    }
                }

            }
        }
    }
}
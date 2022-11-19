package com.bobbyesp.spowlo.presentation.ui.pages.downloader_page

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
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
                if(!logged) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(onClick = {
                            downloadViewModel.spotifyPkceLogin(activity)
                        }) {
                            Text("Connect to Spotify (spotify-web-api-kotlin integration, PKCE auth)")
                        }
                    }
                } else{
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            SearchSongTextBox(
                                songName = downloadViewModel.searchQuery.value,
                                onValueChange = downloadViewModel::onSearch
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSongTextBox(
    songName: String,
    onValueChange: (String) -> Unit
){
    OutlinedTextField(
        value = songName,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text("Search song")
        },
        leadingIcon = {
            Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
        },
        trailingIcon = {
            if(songName.isNotEmpty()){
                IconButton(onClick = {
                    onValueChange("")
                }) {
                    Icon(imageVector = Icons.Rounded.Clear, contentDescription = null)
                }
            }
        },
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            leadingIconColor = MaterialTheme.colorScheme.primary,
            trailingIconColor = MaterialTheme.colorScheme.primary,
            textColor = MaterialTheme.colorScheme.primary,
            disabledLabelColor = MaterialTheme.colorScheme.primary,
            disabledBorderColor = MaterialTheme.colorScheme.primary,
            disabledLeadingIconColor = MaterialTheme.colorScheme.primary,
            disabledTrailingIconColor = MaterialTheme.colorScheme.primary,
            errorBorderColor = MaterialTheme.colorScheme.primary,
            errorLabelColor = MaterialTheme.colorScheme.primary,
            errorCursorColor = MaterialTheme.colorScheme.primary,
            errorLeadingIconColor = MaterialTheme.colorScheme.primary,
            errorTrailingIconColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.background,
        )
    )
}
package com.bobbyesp.spowlo.ui.pages.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.checkIfLoggedIn
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.topbars.SmallTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    viewModel: HomePageViewModel
) {
    val navController = LocalNavController.current

    var loggedIn by remember {
        mutableStateOf(checkIfLoggedIn())
    }

    LaunchedEffect(Unit) {
        loggedIn = checkIfLoggedIn()
    }

    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate(Route.SettingsNavigator.route)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings Icon",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!loggedIn) {
                Button(onClick = {
                    MainActivity.startPkceLoginFlow()
                }) {
                    Text(text = "Launch PKCE Auth flow")
                }
            } else {
                Column {
                    viewState.value.metadataState?.let {
                        Column {
                            Text(text = "Track: ${it.trackName}")
                            Text(text = "Artist: ${it.artistName}")
                            Text(text = "Album: ${it.albumName}")
                        }
                    }
                    viewState.value.playbackState?.let {
                        Column {
                            Text(text = "Playback State: $it")
                            Text(text = "Position (ms): ${it.positionInMs}")
                        }
                    }
                    viewState.value.queueState?.let {
                        Column {
                            Text(text = "Queue time sent (ms): ${it.timeSentInMs}")
                        }
                    }
                }
            }
        }
    }
}
package com.bobbyesp.spowlo.presentation.ui.pages.downloader_page

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.Spowlo.Companion.context

import com.bobbyesp.spowlo.data.auth.AuthModel
import com.bobbyesp.spowlo.presentation.ui.components.songs.TrackItem
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(
    ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class
)
@Composable
fun SearcherPage(
    navController: NavController,
    searcherViewModel: SearcherViewModel = hiltViewModel(),
    activity: Activity? = null
) {
    lateinit var model: AuthModel
    val viewState = searcherViewModel.stateFlow.collectAsState()

    with(viewState.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                color = MaterialTheme.colorScheme.background,
            ) {
                /*if (!logged) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            searcherViewModel.spotifyPkceLogin(activity)
                        }) {
                            Text(stringResource(id = R.string.login))
                        }
                    }
                } else {*/
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.Top
                        ) {
                            SearchSongTextBox(
                                songName = searcherViewModel.searchQuery.value,
                                onValueChange = searcherViewModel::onSearch,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (isSearching) {
                                //circular progress indicator in the middle of the screen
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(8.dp)
                                        .size(32.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )

                            } else {
                                LazyColumn {
                                    items(
                                        items = listOfTracks, itemContent = { track ->
                                            TrackItem(track = track, onClick = {
                                                val browserIntent =
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(track.externalUrls.first { it.name == "spotify" }.url)
                                                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                ContextCompat.startActivity(
                                                    context,
                                                    browserIntent,
                                                    null
                                                )
                                            })
                                            Divider()
                                        })
                                }
                            }
                        }
                    }
                }
            }
        }
        //when navigated launch effect
        LaunchedEffect(key1 = navController.currentBackStackEntry) {
            searcherViewModel.setup()
        }
    }
//}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSongTextBox(
    songName: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = songName,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        placeholder = {
            Text(stringResource(id = R.string.search_song))
        },
        leadingIcon = {
            Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
        },
        trailingIcon = {
            if (songName.isNotEmpty()) {
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
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    )
}
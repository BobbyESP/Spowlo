package com.bobbyesp.spowlo.ui.pages.settings.spotify

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.PermIdentity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.LargeTopAppBar
import com.bobbyesp.spowlo.ui.components.PreferenceInfo
import com.bobbyesp.spowlo.ui.components.PreferenceSubtitle
import com.bobbyesp.spowlo.ui.components.settings.SettingsItemNew
import com.bobbyesp.spowlo.ui.components.settings.SettingsSwitch
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.SPOTIFY_CLIENT_ID
import com.bobbyesp.spowlo.utils.SPOTIFY_CLIENT_SECRET
import com.bobbyesp.spowlo.utils.USE_SPOTIFY_CREDENTIALS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotifySettingsPage(onBackPressed: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )

    var spotifyClientId by remember {
        mutableStateOf(
            PreferencesUtil.getValue(
                SPOTIFY_CLIENT_ID
            )
        )
    }

    var spotifyClientSecret by remember {
        mutableStateOf(
            PreferencesUtil.getValue(
                SPOTIFY_CLIENT_SECRET
            )
        )
    }

    var useSpotifyCredentials by remember {
        mutableStateOf(PreferencesUtil.getValue(USE_SPOTIFY_CREDENTIALS))
    }

    var showClientIdDialog by remember { mutableStateOf(false) }
    var showClientSecretDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.spotify_settings),
                        fontWeight = FontWeight.Bold
                    )
                }, navigationIcon = {
                    BackButton {
                        onBackPressed()
                    }
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = {
                        showHelpDialog = !showHelpDialog
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.HelpOutline,
                            contentDescription = stringResource(R.string.how_does_it_work)
                        )
                    }
                }
            )
        }, content = {
            LazyColumn(
                Modifier
                    .padding(it)
                    .padding(horizontal = 20.dp)
            ) {
                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.general))
                }
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                3.dp
                            )
                        )
                    ) {
                        SettingsSwitch(
                            title = {
                                Text(
                                    stringResource(id = R.string.use_spotify_credentials),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            checked = useSpotifyCredentials,
                            onCheckedChange = {
                                useSpotifyCredentials = !useSpotifyCredentials
                                PreferencesUtil.updateValue(
                                    USE_SPOTIFY_CREDENTIALS,
                                    useSpotifyCredentials
                                )
                            },
                            addTonalElevation = true
                        )
                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                        SettingsItemNew(
                            title = {
                                Text(
                                    stringResource(id = R.string.spotify_client_id),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            description = {
                                Text(stringResource(id = R.string.spotify_client_id_description))
                            },
                            icon = Icons.Outlined.PermIdentity,
                            onClick = {
                                showClientIdDialog = true
                            },
                            enabled = useSpotifyCredentials,
                            addTonalElevation = true
                        )

                        SettingsItemNew(
                            title = {
                                Text(
                                    stringResource(id = R.string.spotify_client_secret),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            description = {
                                Text(stringResource(id = R.string.spotify_client_secret_description))
                            },
                            icon = Icons.Outlined.Key,
                            onClick = {
                                showClientSecretDialog = true
                            },
                            enabled = useSpotifyCredentials,
                            addTonalElevation = true
                        )
                    }
                }
                item {
                    HorizontalDivider(Modifier.padding(vertical = 6.dp))
                    PreferenceInfo(
                        modifier = Modifier
                            .padding(horizontal = 4.dp),
                        text = stringResource(id = R.string.spotify_credentials_info)
                    )
                }
            }
        })
    if (showClientIdDialog) {
        SpotifyClientIDDialog {
            showClientIdDialog = !showClientIdDialog
        }
    }
    if (showClientSecretDialog) {
        SpotifyClientSecretDialog {
            showClientSecretDialog = !showClientSecretDialog
        }
    }
    if (showHelpDialog) {
        SpotifySettingsPageInfoDialog {
            showHelpDialog = !showHelpDialog
        }
    }
}

@Composable
fun SpotifySettingsPageInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(id = R.string.spotify_api_info))
        },
        text = {
            Text(stringResource(id = R.string.spotify_api_info_description))
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.agree))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.HelpOutline,
                contentDescription = stringResource(id = R.string.how_does_it_work)
            )
        }
    )
}
package com.bobbyesp.spowlo.ui.pages.settings.spotify

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.PermIdentity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.LargeTopAppBar
import com.bobbyesp.spowlo.ui.components.PreferenceItem
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.SPOTIFY_CLIENT_ID
import com.bobbyesp.spowlo.utils.SPOTIFY_CLIENT_SECRET

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

    var showClientIdDialog by remember { mutableStateOf(false) }
    var showClientSecretDialog by remember { mutableStateOf(false) }

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
                    )
                }, navigationIcon = {
                    BackButton {
                        onBackPressed()
                    }
                }, scrollBehavior = scrollBehavior
            )
        }, content = {
            LazyColumn(Modifier.padding(it)) {
                item {
                    PreferenceItem(
                        title = stringResource(id = R.string.spotify_client_id),
                        description = stringResource(id = R.string.spotify_client_id_description),
                        icon = Icons.Outlined.PermIdentity,
                        onClick = {
                            showClientIdDialog = true
                        }
                    )
                    PreferenceItem(
                        title = stringResource(id = R.string.spotify_client_secret),
                        description = stringResource(id = R.string.spotify_client_secret_description),
                        icon = Icons.Outlined.Key,
                        onClick = {
                            showClientSecretDialog = true
                        }
                    )
                }

            }
        })
    if(showClientIdDialog) {
        SpotifyClientIDDialog {
            showClientIdDialog = !showClientIdDialog
        }
    }
    if(showClientSecretDialog) {
        SpotifyClientSecretDialog {
            showClientSecretDialog = !showClientSecretDialog
        }
    }
}
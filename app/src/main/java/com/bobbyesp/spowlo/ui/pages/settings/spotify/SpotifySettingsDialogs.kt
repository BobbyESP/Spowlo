package com.bobbyesp.spowlo.ui.pages.settings.spotify

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeveloperMode
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.PermIdentity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.stringState
import com.bobbyesp.spowlo.ui.components.ConfirmButton
import com.bobbyesp.spowlo.ui.components.DismissButton
import com.bobbyesp.spowlo.ui.components.OutlinedButtonChip
import com.bobbyesp.spowlo.utils.ChromeCustomTabsUtil
import com.bobbyesp.spowlo.utils.PreferencesUtil.updateString
import com.bobbyesp.spowlo.utils.SPOTIFY_CLIENT_ID
import com.bobbyesp.spowlo.utils.SPOTIFY_CLIENT_SECRET

val spotifyDeveloperConsoleUrl = "https://developer.spotify.com/dashboard/applications"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SpotifyClientIDDialog(onDismissRequest: () -> Unit) {
    var clientID by SPOTIFY_CLIENT_ID.stringState
    val focusManager = LocalFocusManager.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(id = R.string.spotify_client_id)) },
        icon = { Icon(Icons.Outlined.PermIdentity, null) },
        text = {
            Column() {
                OutlinedTextField(
                    modifier = Modifier.padding(bottom = 8.dp),
                    value = clientID,
                    onValueChange = { clientID = it },
                    label = {
                        Text(stringResource(id = R.string.spotify_client_id))
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                OutlinedButtonChip(
                    onClick = {
                        ChromeCustomTabsUtil.openUrl(
                            spotifyDeveloperConsoleUrl
                        )
                    }, label = stringResource(id = R.string.spotify_developer_console),
                    icon = Icons.Outlined.DeveloperMode
                )
            }
        }, confirmButton = {
            ConfirmButton() {
                SPOTIFY_CLIENT_ID.updateString(clientID)
                onDismissRequest()
            }
        }, dismissButton = {
            DismissButton() {
                onDismissRequest()
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SpotifyClientSecretDialog(onDismissRequest: () -> Unit) {
    var clientSecret by SPOTIFY_CLIENT_SECRET.stringState
    val focusManager = LocalFocusManager.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(id = R.string.spotify_client_secret)) },
        icon = { Icon(Icons.Outlined.Key, null) },
        text = {
            Column() {
                OutlinedTextField(
                    modifier = Modifier.padding(bottom = 8.dp),
                    value = clientSecret,
                    onValueChange = { clientSecret = it },
                    label = {
                        Text(stringResource(id = R.string.spotify_client_secret))
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                OutlinedButtonChip(
                    onClick = {
                        ChromeCustomTabsUtil.openUrl(
                            spotifyDeveloperConsoleUrl
                        )
                    }, label = stringResource(id = R.string.spotify_developer_console),
                    icon = Icons.Outlined.DeveloperMode
                )
            }
        }, confirmButton = {
            ConfirmButton() {
                SPOTIFY_CLIENT_SECRET.updateString(clientSecret)
                onDismissRequest()
            }
        }, dismissButton = {
            DismissButton() {
                onDismissRequest()
            }
        })
}
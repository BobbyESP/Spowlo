package com.bobbyesp.spowlo.ui.pages.settings.format

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.ConfirmButton
import com.bobbyesp.spowlo.ui.components.SingleChoiceItemWithIcon
import com.bobbyesp.spowlo.utils.AUDIO_PROVIDER
import com.bobbyesp.spowlo.utils.PreferencesUtil

@Composable
fun AudioProviderDialog(
    onDismissRequest: () -> Unit
) {
    var audioProvider by remember { mutableStateOf(PreferencesUtil.getAudioProvider()) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(id = R.string.audio_provider)) },
        icon = { Icon(Icons.Outlined.MusicNote, null) },
        text = {
            Column() {
                Text(
                    stringResource(id = R.string.audio_provider_desc),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .padding(horizontal = 24.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                LazyColumn {
                    for (i in 0..1) {
                        item {
                            SingleChoiceItemWithIcon(
                                text = PreferencesUtil.getAudioProviderDesc(i),
                                selected = audioProvider == i,
                                icon = PreferencesUtil.getAudioProviderIcon(i),
                                onClick = {
                                    audioProvider = i
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            ConfirmButton(
                onClick = {
                    PreferencesUtil.encodeInt(AUDIO_PROVIDER, audioProvider)
                    onDismissRequest()
                }
            )
        },
        dismissButton = { TextButton(onClick = { onDismissRequest() }) {
            Text(text = stringResource(id = R.string.dismiss))
        } },
    )

}
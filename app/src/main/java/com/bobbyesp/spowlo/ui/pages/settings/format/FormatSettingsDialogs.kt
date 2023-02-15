package com.bobbyesp.spowlo.ui.pages.settings.format

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.HighQuality
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.SingleChoiceItem
import com.bobbyesp.spowlo.utils.AUDIO_FORMAT
import com.bobbyesp.spowlo.utils.AUDIO_QUALITY
import com.bobbyesp.spowlo.utils.PreferencesUtil

@Composable
fun AudioFormatDialog(onDismissRequest: () -> Unit, onConfirm: () -> Unit = {}) {
    var audioFormat by remember { mutableStateOf(PreferencesUtil.getAudioFormat()) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.dismiss))
            }
        },
        icon = { Icon(Icons.Outlined.AudioFile, null) },
        title = {
            Text(stringResource(R.string.audio_format))
        }, confirmButton = {
            TextButton(onClick = {
                PreferencesUtil.encodeInt(AUDIO_FORMAT, audioFormat)
                onConfirm()
                onDismissRequest()
            }) {
                Text(text = stringResource(R.string.confirm))
            }
        }, text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    text = stringResource(R.string.audio_format_desc),
                    style = MaterialTheme.typography.bodyLarge
                )
                for (i in 0..5)
                    SingleChoiceItem(
                        text = PreferencesUtil.getAudioFormatDesc(i),
                        selected = audioFormat == i
                    ) { audioFormat = i }
            }
        })
}

@Composable
fun AudioQualityDialog(onDismissRequest: () -> Unit, onConfirm: () -> Unit = {}) {
    var audioQuality by remember { mutableStateOf(PreferencesUtil.getAudioQuality()) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.dismiss))
            }
        },
        icon = { Icon(Icons.Outlined.HighQuality, null) },
        title = {
            Text(stringResource(R.string.audio_quality))
        }, confirmButton = {
            TextButton(onClick = {
                PreferencesUtil.encodeInt(AUDIO_QUALITY, audioQuality)
                onConfirm()
                onDismissRequest()
            }) {
                Text(text = stringResource(R.string.confirm))
            }
        }, text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    text = stringResource(R.string.audio_quality_desc),
                    style = MaterialTheme.typography.bodyLarge
                )
                for (i in 0..17)
                    SingleChoiceItem(
                        text = PreferencesUtil.getAudioQualityDesc(i),
                        selected = audioQuality == i
                    ) { audioQuality = i }
            }
        })
}


package com.bobbyesp.spowlo.ui.pages.settings.format

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.HighQuality
import androidx.compose.material.icons.outlined.ShuffleOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.LargeTopAppBar
import com.bobbyesp.spowlo.ui.components.PreferenceSubtitle
import com.bobbyesp.spowlo.ui.components.settings.SettingsItemNew
import com.bobbyesp.spowlo.ui.components.settings.SettingsSwitch
import com.bobbyesp.spowlo.utils.ORIGINAL_AUDIO
import com.bobbyesp.spowlo.utils.PreferencesUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsFormatsPage(onBackPressed: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })

    var audioFormat by remember { mutableStateOf(PreferencesUtil.getAudioFormatDesc()) }
    var audioQuality by remember { mutableStateOf(PreferencesUtil.getAudioQualityDesc()) }
    var preserveOriginalAudio by remember { mutableStateOf(PreferencesUtil.getValue(ORIGINAL_AUDIO)) }

    var showAudioFormatDialog by remember { mutableStateOf(false) }
    var showAudioQualityDialog by remember { mutableStateOf(false) }
    var showAudioProviderDialog by remember { mutableStateOf(false) }


    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(title = {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.format),
                    fontWeight = FontWeight.Bold
                )
            }, navigationIcon = {
                BackButton {
                    onBackPressed()
                }
            }, scrollBehavior = scrollBehavior
            )
        },
        content = {
            LazyColumn(
                Modifier
                    .padding(it)
                    .padding(horizontal = 16.dp)) {
                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.audio))
                }
                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            preserveOriginalAudio = !preserveOriginalAudio
                            PreferencesUtil.updateValue(ORIGINAL_AUDIO, preserveOriginalAudio)
                        },
                        checked = preserveOriginalAudio,
                        title = {
                            Text(
                                text = stringResource(id = R.string.preserve_original_audio),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        description = { Text(text = stringResource(id = R.string.preserve_original_audio_desc)) },
                        icon = Icons.Outlined.Audiotrack,
                        modifier = Modifier.clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    )
                }
                item {
                    SettingsItemNew(title = {
                        Text(
                            text = stringResource(id = R.string.audio_format),
                            fontWeight = FontWeight.Bold
                        )
                    },
                        description = { Text(text = audioFormat) },
                        icon = Icons.Outlined.AudioFile,
                        onClick = { showAudioFormatDialog = true })
                }
                item {
                    SettingsItemNew(
                        title = {
                            Text(
                                text = stringResource(id = R.string.audio_quality),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        description = { Text(text = audioQuality) },
                        icon = Icons.Outlined.HighQuality,
                        onClick = { showAudioQualityDialog = true },
                        enabled = !preserveOriginalAudio,
                    )
                }
                item {
                    SettingsItemNew(
                        title = {
                            Text(
                                text = stringResource(id = R.string.audio_provider),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        description = { Text(text = stringResource(id = R.string.audio_provider_desc)) },
                        icon = Icons.Outlined.ShuffleOn,
                        onClick = { showAudioProviderDialog = true },
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                bottomStart = 8.dp, bottomEnd = 8.dp
                            )
                        )
                    )
                }
            }
        })
    if (showAudioFormatDialog) {
        AudioFormatDialog(onDismissRequest = { showAudioFormatDialog = false }) {
            audioFormat = PreferencesUtil.getAudioFormatDesc()
        }
    }
    if (showAudioQualityDialog) {
        AudioQualityDialog(onDismissRequest = { showAudioQualityDialog = false }) {
            audioQuality = PreferencesUtil.getAudioQualityDesc()
        }
    }
    if (showAudioProviderDialog) {
        AudioProviderDialog(onDismissRequest = { showAudioProviderDialog = false })
    }

}
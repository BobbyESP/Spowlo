package com.bobbyesp.spowlo.ui.pages.settings.downloader

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.HighQuality
import androidx.compose.material.icons.outlined.Lyrics
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Output
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.AudioFilterChip
import com.bobbyesp.spowlo.ui.components.ConfirmButton
import com.bobbyesp.spowlo.ui.components.SingleChoiceItem
import com.bobbyesp.spowlo.utils.AUDIO_FORMAT
import com.bobbyesp.spowlo.utils.AUDIO_PROVIDERS
import com.bobbyesp.spowlo.utils.AUDIO_QUALITY
import com.bobbyesp.spowlo.utils.ChromeCustomTabsUtil
import com.bobbyesp.spowlo.utils.LYRIC_PROVIDERS
import com.bobbyesp.spowlo.utils.OUTPUT_FORMAT
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.audioProvidersList
import com.bobbyesp.spowlo.utils.lyricProvidersList
import com.bobbyesp.spowlo.utils.outputFormatList

@Composable
fun AudioFormatDialog(onDismissRequest: () -> Unit, onConfirm: () -> Unit = {}) {
    var audioFormat by remember { mutableStateOf(PreferencesUtil.getAudioFormat()) }

    AlertDialog(onDismissRequest = onDismissRequest, dismissButton = {
        TextButton(onClick = onDismissRequest) {
            Text(stringResource(R.string.dismiss))
        }
    }, icon = { Icon(Icons.Outlined.AudioFile, null) }, title = {
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
            for (i in 0..5) SingleChoiceItem(
                text = PreferencesUtil.getAudioFormatDesc(i), selected = audioFormat == i
            ) { audioFormat = i }
        }
    })
}

@Composable
fun AudioQualityDialog(onDismissRequest: () -> Unit, onConfirm: () -> Unit = {}) {
    var audioQuality by remember { mutableStateOf(PreferencesUtil.getAudioQuality()) }
    AlertDialog(onDismissRequest = onDismissRequest, dismissButton = {
        TextButton(onClick = onDismissRequest) {
            Text(stringResource(R.string.dismiss))
        }
    }, icon = { Icon(Icons.Outlined.HighQuality, null) }, title = {
        Text(stringResource(R.string.audio_quality))
    }, confirmButton = {
        TextButton(onClick = {
            Log.d("FormatSettingsDialog", "The chosen audioQuality is: $audioQuality")
            PreferencesUtil.encodeInt(AUDIO_QUALITY, audioQuality)
            Log.d(
                "FormatSettingsDialog",
                "The encoded int to the AUDIO_QUALITY settings var is: ${PreferencesUtil.getAudioQuality()}"
            )
            onConfirm()
            onDismissRequest()
        }) {
            Text(text = stringResource(R.string.confirm))
        }
    }, text = {
        Column(modifier = Modifier) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                text = stringResource(R.string.audio_quality_desc),
                style = MaterialTheme.typography.bodyLarge
            )
            LazyColumn(
                content = {
                    for (i in 17 downTo 0) item {
                        SingleChoiceItem(
                            text = PreferencesUtil.getAudioQualityDesc(i),
                            selected = audioQuality == i
                        ) {
                            audioQuality = i
                            Log.d(
                                "FormatSettingsDialog", "Changed to $i"
                            )
                        }
                    }
                }, modifier = Modifier.size(400.dp)
            )
        }
    })
}

@Composable
fun AudioProviderDialog(
    onDismissRequest: () -> Unit
) {
    var audioProviders by remember { mutableStateOf(PreferencesUtil.getAudioProvider()) }

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
                        .padding(bottom = 12.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                LazyColumn {
                    itemsIndexed(audioProvidersList) { index, providerKey ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = audioProviders.contains(providerKey),
                                onCheckedChange = { isChecked ->
                                    audioProviders = if (isChecked) {
                                        if (audioProviders.isEmpty()) {
                                            listOf(providerKey)
                                        } else {
                                            audioProviders + providerKey
                                        }
                                    } else {
                                        audioProviders - providerKey
                                    }
                                },
                                modifier = Modifier.padding(8.dp)
                            )
                            Text(
                                text = providerKey,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            ConfirmButton(
                onClick = {
                    val selectedProvidersString = audioProviders.joinToString(separator = ",")
                    PreferencesUtil.encodeString(AUDIO_PROVIDERS, selectedProvidersString)
                    onDismissRequest()
                }
            )
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = stringResource(id = R.string.dismiss))
            }
        },
    )

}

@Composable
fun LyricProviderDialog(
    onDismissRequest: () -> Unit
) {
    var lyricProviders by remember { mutableStateOf(PreferencesUtil.getLyricProviders()) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(id = R.string.lyric_providers)) },
        icon = { Icon(Icons.Outlined.Lyrics, null) },
        text = {
            Column {
                Text(
                    stringResource(id = R.string.lyric_providers_desc),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                LazyColumn {
                    itemsIndexed(lyricProvidersList) { index, providerKey ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = lyricProviders.contains(providerKey),
                                onCheckedChange = { isChecked ->
                                    lyricProviders = if (isChecked) {
                                        if (lyricProviders.isEmpty()) {
                                            listOf(providerKey)
                                        } else {
                                            lyricProviders + providerKey
                                        }
                                    } else {
                                        lyricProviders - providerKey
                                    }
                                },
                                modifier = Modifier.padding(8.dp)
                            )
                            Text(
                                text = providerKey,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            ConfirmButton(
                onClick = {
                    val selectedProvidersString = lyricProviders.joinToString(separator = ",")
                    PreferencesUtil.encodeString(LYRIC_PROVIDERS, selectedProvidersString)
                    onDismissRequest()
                }
            )
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = stringResource(id = R.string.dismiss))
            }
        }
    )
}

@Composable
fun OutputFormatDialog(
    onDismissRequest: () -> Unit
) {
    var outputFormat by remember { mutableStateOf(PreferencesUtil.getOutputFormat()) }
    var outputFormatText by remember { mutableStateOf(outputFormat.joinToString("/")) }
    val chunked = outputFormatList.chunked((outputFormatList.size + 1) / 2)

    LaunchedEffect(outputFormat) {
        outputFormatText = outputFormat.joinToString("/")
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(id = R.string.output_format)) },
        icon = { Icon(Icons.Outlined.Output, null) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    stringResource(id = R.string.output_format_desc),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Gray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnnotatedBasicTextField(
                        value = AnnotatedString(
                            text = if (outputFormatText.isEmpty()) {
                                "download_dir/{title}.{output-ext}"
                            } else {
                                "download_dir" + outputFormatText + "/{title}.{output-ext}"
                            },
                            spanStyles = listOf(
                                AnnotatedString.Range(
                                    item = SpanStyle(color = MaterialTheme.colorScheme.primary),
                                    start = "download_dir".length,
                                    end = "download_dir".length + outputFormatText.length
                                )
                            )
                        ),
                        onValueChange = {
                            outputFormatText = it.text
                            outputFormat = it.text.split("/")
                        },
                        singleLine = false,
                        readOnly = true,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start),
                        modifier = Modifier.fillMaxWidth().height(100.dp).padding(8.dp)
                            .width(100.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(id = R.string.available_output_formats),
                            modifier = Modifier.padding(end = 4.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(
                            onClick = {
                                ChromeCustomTabsUtil.openUrl("https://spotdl.github.io/spotify-downloader/usage/#output-variables")
                            }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    chunked[0].indices.forEach { index ->
                        Row {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                chunked[0][index].let { element ->
                                    val isSelected = outputFormat.contains(element)
                                    AudioFilterChip(
                                        label = element,
                                        selected = isSelected,
                                        onClick = {
                                            if (isSelected) {
                                                outputFormat =
                                                    outputFormat.filterNot { it == element }
                                            } else {
                                                outputFormat = outputFormat + element
                                            }
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            if (chunked.size > 1) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    chunked[1][index].let { element ->
                                        val isSelected = outputFormat.contains(element)
                                        AudioFilterChip(
                                            label = element,
                                            selected = isSelected,
                                            onClick = {
                                                if (isSelected) {
                                                    outputFormat =
                                                        outputFormat.filterNot { it == element }
                                                } else {
                                                    outputFormat = outputFormat + element
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            ConfirmButton(
                onClick = {
                    val selectedFormatString = outputFormat.joinToString(separator = ",")
                    PreferencesUtil.encodeString(OUTPUT_FORMAT, selectedFormatString)
                    onDismissRequest()
                }
            )
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = stringResource(id = R.string.dismiss))
            }
        }
    )
}

@Composable
fun AnnotatedBasicTextField(
    value: AnnotatedString,
    onValueChange: (AnnotatedString) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current
) {
    BasicTextField(
        value = value.text,
        onValueChange = { onValueChange(AnnotatedString(it)) },
        singleLine = singleLine,
        readOnly = readOnly,
        textStyle = textStyle,
        modifier = modifier
    )
    Text(
        text = value,
        modifier = modifier
    )
}
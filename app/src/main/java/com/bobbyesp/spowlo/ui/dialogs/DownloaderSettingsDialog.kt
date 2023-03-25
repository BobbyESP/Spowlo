package com.bobbyesp.spowlo.ui.dialogs

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Dataset
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.BottomDrawer
import com.bobbyesp.spowlo.ui.components.DismissButton
import com.bobbyesp.spowlo.ui.components.FilledButtonWithIcon
import com.bobbyesp.spowlo.ui.components.OutlinedButtonWithIcon
import com.bobbyesp.spowlo.ui.pages.settings.format.AudioFormatDialog
import com.bobbyesp.spowlo.ui.pages.settings.format.AudioQualityDialog
import com.bobbyesp.spowlo.ui.pages.settings.spotify.SpotifyClientIDDialog
import com.bobbyesp.spowlo.ui.pages.settings.spotify.SpotifyClientSecretDialog
import com.bobbyesp.spowlo.utils.COOKIES
import com.bobbyesp.spowlo.utils.DONT_FILTER_RESULTS
import com.bobbyesp.spowlo.utils.GEO_BYPASS
import com.bobbyesp.spowlo.utils.ORIGINAL_AUDIO
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.SKIP_INFO_FETCH
import com.bobbyesp.spowlo.utils.SYNCED_LYRICS
import com.bobbyesp.spowlo.utils.USE_CACHING
import com.bobbyesp.spowlo.utils.USE_SPOTIFY_CREDENTIALS
import com.bobbyesp.spowlo.utils.USE_YT_METADATA

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun DownloaderSettingsDialog(
    useDialog: Boolean = false,
    dialogState: Boolean = false,
    isShareActivity: Boolean = false,
    drawerState: ModalBottomSheetState,
    confirm: () -> Unit,
    hide: () -> Unit,
    onRequestMetadata: () -> Unit,
) {
    val settings = PreferencesUtil

    var preserveOriginalAudio by remember {
        mutableStateOf(
            settings.getValue(
                ORIGINAL_AUDIO
            )
        )
    }

    var useSpotifyCredentials by remember {
        mutableStateOf(
            settings.getValue(
                USE_SPOTIFY_CREDENTIALS
            )
        )
    }

    var useYtMetadata by remember {
        mutableStateOf(
            settings.getValue(
                USE_YT_METADATA
            )
        )
    }

    var useCookies by remember {
        mutableStateOf(
            settings.getValue(
                COOKIES
            )
        )
    }

    var useCaching by remember {
        mutableStateOf(
            settings.getValue(
                USE_CACHING
            )
        )
    }

    var dontFilter by remember {
        mutableStateOf(
            settings.getValue(
                DONT_FILTER_RESULTS
            )
        )
    }

    var useSyncedLyrics by remember {
        mutableStateOf(
            settings.getValue(SYNCED_LYRICS)
        )
    }

    var useGeoBypass by remember {
        mutableStateOf(
            settings.getValue(
                GEO_BYPASS
            )
        )
    }

    var skipInfoFetch by remember { mutableStateOf(settings.getValue(SKIP_INFO_FETCH)) }

    var showAudioFormatDialog by remember { mutableStateOf(false) }
    var showAudioQualityDialog by remember { mutableStateOf(false) }
    var showClientIdDialog by remember { mutableStateOf(false) }
    var showClientSecretDialog by remember { mutableStateOf(false) }

    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()


    val downloadButtonCallback = {
        hide()
        confirm()
    }

    val requestMetadata = {
        hide()
        onRequestMetadata()
    }

    val sheetContent: @Composable () -> Unit = {
        /*Column {
            Text(
                text = stringResource(R.string.settings_before_download_text),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Start)
            )
            AnimatedVisibility(visible = preserveOriginalAudio) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.surfaceVariant),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            modifier = Modifier.padding(6.dp),
                            tint = MaterialTheme.colorScheme.run { onSecondaryContainer }
                        )
                        Text(
                            text = stringResource(R.string.preserve_original_audio_warning),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
            DrawerSheetSubtitle(text = stringResource(id = R.string.general_settings))
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ),
            ) {
                AudioFilterChip(
                    label = stringResource(id = R.string.preserve_original_audio),
                    animated = true,
                    selected = preserveOriginalAudio,
                    onClick = {
                        preserveOriginalAudio = !preserveOriginalAudio
                        scope.launch {
                            settings.updateValue(ORIGINAL_AUDIO, preserveOriginalAudio)
                        }
                    }
                )
                ButtonChip(
                    label = stringResource(id = R.string.audio_format),
                    icon = Icons.Outlined.AudioFile,
                    onClick = { showAudioFormatDialog = true },
                )
                ButtonChip(
                    label = stringResource(id = R.string.audio_quality),
                    icon = Icons.Outlined.HighQuality,
                    enabled = !preserveOriginalAudio,
                    onClick = { showAudioQualityDialog = true },
                )
            }
            DrawerSheetSubtitle(text = stringResource(id = R.string.spotify))
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ),
            ) {
                AudioFilterChip(
                    label = stringResource(id = R.string.use_spotify_credentials),
                    animated = true,
                    selected = useSpotifyCredentials,
                    onClick = {
                        useSpotifyCredentials = !useSpotifyCredentials
                        scope.launch {
                            settings.updateValue(USE_SPOTIFY_CREDENTIALS, useSpotifyCredentials)
                        }
                    }
                )
                ButtonChip(
                    label = stringResource(id = R.string.client_id),
                    icon = Icons.Outlined.Person,
                    enabled = useSpotifyCredentials,
                    onClick = { showClientIdDialog = true },
                )
                ButtonChip(
                    label = stringResource(id = R.string.client_secret),
                    icon = Icons.Outlined.Key,
                    enabled = useSpotifyCredentials,
                    onClick = { showClientSecretDialog = true },
                )
            }

            DrawerSheetSubtitle(text = stringResource(id = R.string.experimental_features))
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ),
            ) {
                AudioFilterChip(
                    label = stringResource(id = R.string.synced_lyrics),
                    animated = true,
                    selected = useSyncedLyrics,
                    onClick = {
                        useSyncedLyrics = !useSyncedLyrics
                        scope.launch {
                            settings.updateValue(SYNCED_LYRICS, useSyncedLyrics)
                        }
                    }
                )
                AudioFilterChip(
                    label = stringResource(id = R.string.geo_bypass),
                    selected = useGeoBypass,
                    animated = true,
                    onClick = {
                        useGeoBypass = !useGeoBypass
                        scope.launch {
                            settings.updateValue(GEO_BYPASS, useGeoBypass)
                        }
                    }
                )

                AudioFilterChip(
                    label = stringResource(id = R.string.use_cache),
                    animated = true,
                    selected = useCaching,
                    onClick = {
                        useCaching = !useCaching
                        scope.launch {
                            settings.updateValue(USE_CACHING, useCaching)
                        }
                    }
                )

                AudioFilterChip(
                    label = stringResource(id = R.string.dont_filter_results),
                    selected = dontFilter,
                    animated = true,
                    onClick = {
                        dontFilter = !dontFilter
                        scope.launch {
                            settings.updateValue(DONT_FILTER_RESULTS, dontFilter)
                        }
                    }
                )
                AudioFilterChip(
                    label = stringResource(id = R.string.use_cookies),
                    animated = true,
                    selected = useCookies,
                    onClick = {
                        useCookies = !useCookies
                        scope.launch {
                            settings.updateValue(COOKIES, useCookies)
                        }
                    }
                )
                AudioFilterChip(
                    label = stringResource(id = R.string.use_yt_metadata),
                    animated = true,
                    selected = useYtMetadata,
                    onClick = {
                        useYtMetadata = !useYtMetadata
                        scope.launch {
                            settings.updateValue(USE_YT_METADATA, useYtMetadata)
                        }
                    }
                )
            }
        }
         */
    }
    if (!useDialog) {
        //TODO: Change this UI
        BottomDrawer(drawerState = drawerState, sheetContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.DownloadDone,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(R.string.settings_before_download),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(vertical = 16.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
            sheetContent()

            val state = rememberLazyListState()

            LaunchedEffect(drawerState.isVisible) {
                state.scrollToItem(1)
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.End,
                state = state
            ) {
                item {
                    OutlinedButtonWithIcon(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        onClick = hide,
                        icon = Icons.Outlined.Cancel,
                        text = stringResource(R.string.cancel)
                    )
                }
                item {
                    FilledButtonWithIcon(
                        modifier = Modifier.padding(end = 12.dp),
                        onClick = requestMetadata,
                        icon = Icons.Outlined.Dataset,
                        text = stringResource(R.string.request_metadata)
                    )
                }
                item {
                    FilledButtonWithIcon(
                        onClick = downloadButtonCallback,
                        icon = Icons.Outlined.DownloadDone,
                        text = stringResource(R.string.start_download)
                    )
                }
            }
        })
    } else if (dialogState) {
        AlertDialog(
            onDismissRequest = hide,
            confirmButton = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DismissButton { hide() }
                    TextButton(onClick = requestMetadata) {
                        Text(text = stringResource(R.string.request_metadata))
                    }
                    TextButton(onClick = downloadButtonCallback) {
                        Text(text = stringResource(R.string.start_download))
                    }
                }
            },
            dismissButton = { },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.DoneAll,
                    contentDescription = null
                )
            },
            title = {
                Text(
                    stringResource(R.string.settings_before_download),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    sheetContent()
                }
            })
    }
    if (showAudioFormatDialog) {
        AudioFormatDialog(
            onDismissRequest = { showAudioFormatDialog = false },
        )
    }
    if (showAudioQualityDialog) {
        AudioQualityDialog(
            onDismissRequest = { showAudioQualityDialog = false },
        )
    }
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

}
package com.bobbyesp.spowlo.ui.dialogs.bottomsheets

import android.Manifest
import android.os.Build
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Dataset
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.HighQuality
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlaylistAddCheck
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.AudioFilterChip
import com.bobbyesp.spowlo.ui.components.BottomSheet
import com.bobbyesp.spowlo.ui.components.ButtonChip
import com.bobbyesp.spowlo.ui.components.DrawerSheetSubtitle
import com.bobbyesp.spowlo.ui.components.FilledButtonWithIcon
import com.bobbyesp.spowlo.ui.components.OutlinedButtonWithIcon
import com.bobbyesp.spowlo.ui.pages.settings.format.AudioFormatDialog
import com.bobbyesp.spowlo.ui.pages.settings.format.AudioQualityDialog
import com.bobbyesp.spowlo.ui.pages.settings.spotify.SpotifyClientIDDialog
import com.bobbyesp.spowlo.ui.pages.settings.spotify.SpotifyClientSecretDialog
import com.bobbyesp.spowlo.utils.COOKIES
import com.bobbyesp.spowlo.utils.DONT_FILTER_RESULTS
import com.bobbyesp.spowlo.utils.ORIGINAL_AUDIO
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.SKIP_INFO_FETCH
import com.bobbyesp.spowlo.utils.SYNCED_LYRICS
import com.bobbyesp.spowlo.utils.ToastUtil
import com.bobbyesp.spowlo.utils.USE_CACHING
import com.bobbyesp.spowlo.utils.USE_SPOTIFY_CREDENTIALS
import com.bobbyesp.spowlo.utils.USE_YT_METADATA
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun DownloaderBottomSheet(
    onBackPressed: () -> Unit,
    url: String,
    navController: NavController,
    onDownloadPressed: () -> Unit,
    onRequestMetadata: () -> Unit,
    navigateToPlaylist: (String) -> Unit,
    navigateToAlbum: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val pages =
        listOf(BottomSheetPages.MAIN, BottomSheetPages.TERTIARY) //, BottomSheetPages.SECONDARY
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        pages.size
    }

    val roundedTopShape =
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 0.dp)

    val storagePermission = rememberPermissionState(
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) { b: Boolean ->
        if (b) {
            onDownloadPressed()
        } else {
            ToastUtil.makeToast(R.string.permission_denied)
        }
    }

    val checkPermissionOrDownload = {
        if (Build.VERSION.SDK_INT > 29 || storagePermission.status == PermissionStatus.Granted) onDownloadPressed()
        else {
            storagePermission.launchPermissionRequest()
        }
    }

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

    var skipInfoFetch by remember { mutableStateOf(settings.getValue(SKIP_INFO_FETCH)) }

    var showAudioFormatDialog by remember { mutableStateOf(false) }
    var showAudioQualityDialog by remember { mutableStateOf(false) }
    var showClientIdDialog by remember { mutableStateOf(false) }
    var showClientSecretDialog by remember { mutableStateOf(false) }

    val downloadButtonCallback = {
        onBackPressed()
        checkPermissionOrDownload()
    }

    val requestMetadata = {
        onBackPressed()
        onRequestMetadata()
    }

    BottomSheet(onDismiss = onBackPressed) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(8.dp)
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300, easing = FastOutSlowInEasing
                    ),
                )

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.DownloadDone,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp, start = 8.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.settings_before_download),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 12.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = stringResource(R.string.settings_before_download_text),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 8.dp)
            )
            IndicatorBehindScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.animateContentSize(),
                indicator = { tabPositions ->
                    Box(
                        Modifier
                            .padding(vertical = 12.dp)
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    )
                },
                edgePadding = 16.dp,
                tabAlignment = Alignment.CenterStart,
            ) {
                pages.forEachIndexed { index, page ->
                    Tab(
                        text = { Text(text = page) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    )
                }
            }
            HorizontalPager(
                state = pagerState, modifier = Modifier.animateContentSize()
            ) {
                when (pages[it]) {
                    BottomSheetPages.MAIN -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp)
                        ) {
                            DrawerSheetSubtitle(text = stringResource(id = R.string.general))
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState())
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                            ) {
                                AudioFilterChip(label = stringResource(id = R.string.preserve_original_audio),
                                    animated = true,
                                    selected = preserveOriginalAudio,
                                    onClick = {
                                        preserveOriginalAudio = !preserveOriginalAudio
                                        scope.launch {
                                            settings.updateValue(
                                                ORIGINAL_AUDIO,
                                                preserveOriginalAudio
                                            )
                                        }
                                    })
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
                                AudioFilterChip(label = stringResource(id = R.string.use_spotify_credentials),
                                    animated = true,
                                    selected = useSpotifyCredentials,
                                    onClick = {
                                        useSpotifyCredentials = !useSpotifyCredentials
                                        scope.launch {
                                            settings.updateValue(
                                                USE_SPOTIFY_CREDENTIALS, useSpotifyCredentials
                                            )
                                        }
                                    })
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

                        }
                    }

                    BottomSheetPages.SECONDARY -> {

                    }

                    BottomSheetPages.TERTIARY -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp)
                        ) {
                            DrawerSheetSubtitle(text = stringResource(id = R.string.general))
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState())
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                            ) {
                                AudioFilterChip(label = stringResource(id = R.string.use_cache),
                                    animated = true,
                                    selected = useCaching,
                                    onClick = {
                                        useCaching = !useCaching
                                        scope.launch {
                                            settings.updateValue(USE_CACHING, useCaching)
                                        }
                                    })

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
                                AudioFilterChip(label = stringResource(id = R.string.synced_lyrics),
                                    animated = true,
                                    selected = useSyncedLyrics,
                                    onClick = {
                                        useSyncedLyrics = !useSyncedLyrics
                                        scope.launch {
                                            settings.updateValue(SYNCED_LYRICS, useSyncedLyrics)
                                        }
                                    })
                                AudioFilterChip(label = stringResource(id = R.string.dont_filter_results),
                                    selected = dontFilter,
                                    animated = true,
                                    onClick = {
                                        dontFilter = !dontFilter
                                        scope.launch {
                                            settings.updateValue(DONT_FILTER_RESULTS, dontFilter)
                                        }
                                    })
                                AudioFilterChip(label = stringResource(id = R.string.use_cookies),
                                    animated = true,
                                    selected = useCookies,
                                    onClick = {
                                        useCookies = !useCookies
                                        scope.launch {
                                            settings.updateValue(COOKIES, useCookies)
                                        }
                                    })
                                AudioFilterChip(label = stringResource(id = R.string.use_yt_metadata),
                                    animated = true,
                                    selected = useYtMetadata,
                                    onClick = {
                                        useYtMetadata = !useYtMetadata
                                        scope.launch {
                                            settings.updateValue(USE_YT_METADATA, useYtMetadata)
                                        }
                                    })
                            }
                        }
                    }
                }
            }

            val state = rememberLazyListState()

            LaunchedEffect(Unit) {
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
                        onClick = { navController.popBackStack() },
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
                    val playlistPattern =
                        "^https?://open.spotify.com/playlist/([a-zA-Z0-9]+)(\\?.*)?\$"
                    val playlistRegex = Regex(playlistPattern)

                    val albumPattern = "^https?://open.spotify.com/album/([a-zA-Z0-9]+)(\\?.*)?\$"
                    val albumRegex = Regex(albumPattern)

                    when {
                        playlistRegex.matches(url) -> {
                            val playlistId = playlistRegex.find(url)!!.groupValues[1]
                            FilledButtonWithIcon(
                                modifier = Modifier.padding(end = 12.dp),
                                onClick = { navigateToPlaylist(playlistId) },
                                icon = Icons.Outlined.PlaylistAddCheck,
                                text = stringResource(R.string.see_playlist)
                            )
                        }

                        albumRegex.matches(url) -> {
                            val albumId = albumRegex.find(url)!!.groupValues[1]
                            FilledButtonWithIcon(
                                modifier = Modifier.padding(end = 12.dp),
                                onClick = { navigateToAlbum(albumId) },
                                icon = Icons.Outlined.Album,
                                text = stringResource(R.string.see_album)
                            )
                        }

                        else -> {
                            FilledButtonWithIcon(
                                modifier = Modifier.padding(end = 12.dp),
                                onClick = downloadButtonCallback,
                                icon = Icons.Outlined.DownloadDone,
                                text = stringResource(R.string.start_download)
                            )
                        }
                    }
                }
            }
        }
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

object BottomSheetPages {
    val MAIN = getString(R.string.audio)
    val SECONDARY = "secondary"
    val TERTIARY = getString(R.string.downloader)
}

//GET STRING FROM APP.CONTEXT GIVEN A r.string ID
fun getString(id: Int): String {
    return App.context.getString(id)
}

package com.bobbyesp.spowlo.ui.bottomSheets.track

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.adamratzman.spotify.models.SimpleTrack
import com.adamratzman.spotify.models.Track
import com.bobbyesp.miniplayer_service.service.MediaServiceHandler
import com.bobbyesp.miniplayer_service.service.MediaState
import com.bobbyesp.miniplayer_service.service.PlayerEvent
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.data.local.model.SelectedSong
import com.bobbyesp.spowlo.features.downloader.Downloader
import com.bobbyesp.spowlo.features.downloader.DownloaderUtil
import com.bobbyesp.spowlo.features.inapp_notifications.domain.model.Notification
import com.bobbyesp.spowlo.features.inapp_notifications.domain.model.Notification.Companion.toNotification
import com.bobbyesp.spowlo.features.inapp_notifications.domain.model.SpEntityNotificationInfo
import com.bobbyesp.spowlo.features.lyrics_downloader.domain.model.Song
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.MetadataEntity
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.LocalNotificationsManager
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.bottomsheets.BottomSheet
import com.bobbyesp.spowlo.ui.components.lazygrid.GridMenuItem
import com.bobbyesp.spowlo.ui.components.lazygrid.PlayPauseDynamicItem
import com.bobbyesp.spowlo.ui.components.lazygrid.VerticalGridMenu
import com.bobbyesp.spowlo.ui.ext.formatArtistsName
import com.bobbyesp.spowlo.utils.localAsset
import com.bobbyesp.spowlo.utils.notifications.ToastUtil
import com.bobbyesp.spowlo.utils.preferences.PreferencesStrings.STOP_AFTER_CLOSING_BS
import com.bobbyesp.spowlo.utils.preferences.PreferencesUtil.getBoolean
import com.bobbyesp.spowlo.utils.time.TimeUtils.formatDuration
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackBottomSheet(
    track: Track? = null,
    simpleTrack: SimpleTrack? = null,
    artworkForSimpleTrack: String? = null,
    viewModel: TrackBottomSheetViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current
    val navController = LocalNavController.current
    val notificationsManager = LocalNotificationsManager.current

    val stopPlayingAfterClosing = STOP_AFTER_CLOSING_BS.getBoolean()

    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()

    val spotifyUrlNotNull =
        track?.externalUrls?.spotify != null || simpleTrack?.externalUrls?.spotify != null
    val spotifyUrl = track?.externalUrls?.spotify ?: simpleTrack?.externalUrls?.spotify

    val trackName = track?.name ?: simpleTrack?.name ?: ""
    val trackArtists = track?.artists ?: simpleTrack?.artists ?: emptyList()
    val trackArtistsString = trackArtists.formatArtistsName()
    val trackImage = track?.album?.images?.firstOrNull()?.url
    val trackId = track?.id ?: simpleTrack?.id

    val playableUrl = track?.previewUrl ?: simpleTrack?.previewUrl

    BottomSheet(
        onDismiss = {
            onDismiss()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && stopPlayingAfterClosing) viewModel.stopPlaying()
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (trackImage != null) CoilImage(
                imageModel = { trackImage },
                modifier = Modifier
                    .size(50.dp)
                    .aspectRatio(
                        1f, matchHeightConstraintsFirst = true
                    )
                    .clip(MaterialTheme.shapes.extraSmall),
                imageOptions = ImageOptions(
                    contentDescription = stringResource(
                        id = R.string.track_artwork
                    ),
                )
            )
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = trackName, fontWeight = FontWeight.Bold, maxLines = 1
                )
                Text(
                    text = trackArtistsString,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.alpha(alpha = 0.6f),
                    maxLines = 1
                )
            }
        }
        HorizontalDivider()
        VerticalGridMenu(
            modifier = Modifier,
            contentPadding = PaddingValues(0.dp)
        ) {
            GridMenuItem(
                icon = { localAsset(id = R.drawable.spotify_logo) },
                title = { stringResource(id = R.string.open_in_spotify) },
                onClick = {
                    uriHandler.openUri(spotifyUrl!!)
                },
                enabled = spotifyUrlNotNull
            )
            GridMenuItem(
                icon = Icons.Default.Download,
                title = { stringResource(id = R.string.download) },
                onClick = {
                    val downloadInfo = Downloader.DownloadInfo(
                        title = trackName,
                        artist = trackArtistsString,
                        thumbnailUrl = trackImage ?: "",
                        url = spotifyUrl!!,
                        type = SpotifyItemType.TRACKS
                    )
                    val notification = downloadInfo.toNotification()
                    viewModel.downloadSong(
                        downloadInfo = downloadInfo,
                        onSuccess = {
                            notificationsManager.showNotification(
                                Notification(
                                    title = "Download finished",
                                    subtitle = "The song has been downloaded successfully. ${downloadInfo.title} by ${downloadInfo.artist}",
                                    timestamp = System.currentTimeMillis(),
                                    content = null
                                )
                            )
                        },
                        onFailure = {
                            notificationsManager.showNotification(
                                Notification(
                                    title = "Download failed",
                                    subtitle = "The download failed. Please try again.",
                                    timestamp = System.currentTimeMillis(),
                                    content = null
                                )
                            )
                        }
                    )
                    notificationsManager.showNotification(notification)

                }
            )
            GridMenuItem(
                icon = Icons.Default.ContentCopy,
                title = { stringResource(id = R.string.copy_link) },
                onClick = {
                    try {
                        clipboardManager.setText(AnnotatedString(spotifyUrl!!))
                        ToastUtil.makeToast(context, R.string.copied_to_clipboard)
                    } catch (e: Exception) {
                        Log.e("TrackBottomSheet", "Failed to copy link", e)
                        ToastUtil.makeToast(context, R.string.copy_failed)
                        return@GridMenuItem
                    }
                }, enabled = spotifyUrlNotNull
            )
            GridMenuItem(
                icon = Icons.AutoMirrored.Filled.Launch,
                title = { stringResource(id = R.string.open_page) },
                onClick = {
                    onDismiss()

                    val selectedMetadataEntity = MetadataEntity(
                        type = SpotifyItemType.TRACKS,
                        id = trackId!!,
                    )

                    navController.navigate(
                        Route.MetadataEntityViewer.createRoute(
                            selectedMetadataEntity
                        )
                    )
                },
                enabled = trackId != null
            )
            GridMenuItem(
                icon = Icons.Default.Lyrics,
                title = { stringResource(id = R.string.lyrics) },
                onClick = {
                    onDismiss()

                    val mainArtist = trackArtists.first().name ?: ""

                    val selectedSongParcel = SelectedSong(
                        name = trackName,
                        mainArtist = mainArtist,
                        localSongPath = null,
                    )

                    navController.navigate(
                        Route.SelectedSongLyrics.createRoute(
                            selectedSongParcel
                        )
                    )
                }
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val isPlayingAndSameSong =
                    viewState.value.isPlaying && viewState.value.actualSong?.path == playableUrl
                PlayPauseDynamicItem(
                    modifier = Modifier,
                    onClick = {
                        if (isPlayingAndSameSong) {
                            viewModel.playPause()
                        } else {
                            val song = Song(
                                id = 0L,
                                title = trackName,
                                artist = trackArtistsString,
                                album = track?.album?.name ?: "",
                                albumArtPath = if (track != null) Uri.parse(trackImage) else Uri.parse(
                                    artworkForSimpleTrack
                                ),
                                duration = track?.durationMs?.toDouble() ?: 0.0,
                                path = playableUrl!!
                            )
                            viewModel.loadSongAndPlay(
                                song
                            )
                        }
                    },
                    enabled = playableUrl != null,
                    playing = isPlayingAndSameSong,
                    time = viewState.value.progressString
                )
            }
        }
    }
}

@HiltViewModel
class TrackBottomSheetViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val serviceHandler: MediaServiceHandler
) : ViewModel() {

    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            serviceHandler.mediaState.collect { mediaState ->
                when (mediaState) {
                    is MediaState.Buffering -> calculateProgressValues(mediaState.progress)
                    is MediaState.Playing -> mutablePageViewState.update { it.copy(isPlaying = true) }
                    is MediaState.Idle -> mutablePageViewState.update { it.copy(uiState = PlayerState.Initial) }
                    is MediaState.Progress -> calculateProgressValues(mediaState.progress)
                    is MediaState.Ready -> {
                        mutablePageViewState.update {
                            it.copy(
                                uiState = PlayerState.Ready, duration = mediaState.duration
                            )
                        }
                    }
                }
            }
        }
    }

    fun generateNotification(downloadInfo: Downloader.DownloadInfo): Notification =
        with(downloadInfo) {
            Notification(
                title = title,
                subtitle = artist,
                timestamp = System.currentTimeMillis(),
                entityInfo = SpEntityNotificationInfo(
                    name = title,
                    artist = artist,
                    artworkUrl = thumbnailUrl,
                    downloadUrl = url,
                    itemType = SpotifyItemType.TRACKS,
                ),
            )
        }

    fun downloadSong(
        scope: CoroutineScope = viewModelScope,
        downloadInfo: Downloader.DownloadInfo,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            DownloaderUtil.downloadSong(
                downloadInfo = downloadInfo,
                taskId = Downloader.makeKey(downloadInfo.title, downloadInfo.artist)
            ).onSuccess {
                onSuccess()
            }.onFailure {
                onFailure()
            }
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            serviceHandler.killPlayer()
        }
        super.onCleared()
    }

    data class PageViewState(
        val uiState: PlayerState = PlayerState.Initial,
        val progress: Float = 0f,
        val progressString: String = "00:00",
        val duration: Long = 0L,
        val actualSong: Song? = null,
        val isPlaying: Boolean = false
    )

    private fun loadSong(song: Song) {
        mutablePageViewState.update {
            it.copy(
                actualSong = song
            )
        }
        val mediaItem = MediaItem.Builder()
            .setUri(song.path)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .setAlbumTitle(song.album)
                    .setArtworkUri(song.albumArtPath)
                    .build()
            ).build()

        viewModelScope.launch {
            serviceHandler.setMediaItem(mediaItem)
        }
    }

    fun loadSongAndPlay(song: Song) {
        loadSong(song)
        viewModelScope.launch {
            serviceHandler.onPlayerEvent(PlayerEvent.PlayPause)
        }
    }

    fun stopPlaying() {
        viewModelScope.launch {
            serviceHandler.onPlayerEvent(PlayerEvent.Stop)
        }
    }

    fun playPause() {
        viewModelScope.launch {
            serviceHandler.onPlayerEvent(PlayerEvent.PlayPause)
        }
    }

    private fun calculateProgressValues(currentProgress: Long) {
        with(pageViewState.value) {
            val progress = if (currentProgress > 0) (currentProgress.toFloat() / duration) else 0f
            val progressString = formatDuration(currentProgress)
            mutablePageViewState.update {
                it.copy(
                    progress = progress, progressString = progressString
                )
            }
        }
    }
}

sealed class PlayerState {
    data object Initial : PlayerState()
    data object Ready : PlayerState()
}

package com.bobbyesp.spowlo.ui.pages.utilities.media_player

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.MediaStoreReceiver
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import com.bobbyesp.spowlo.ui.components.buttons.PlayPauseAnimatedButton
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.images.PlaceholderCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MediaPlayerPage(
    viewModel: MediaPlayerPageViewModel
) {

    val applicationContext = LocalContext.current.applicationContext

    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()

    var songs by rememberSaveable(key = "test_songs") {
        mutableStateOf<List<Song>>(emptyList())
    }

    var mediaItem by rememberSaveable(key = "test_media_item") {
        mutableStateOf<MediaItem?>(null)
    }

    LaunchedEffect(true) {
        songs = withContext(Dispatchers.IO) {
            MediaStoreReceiver.getAllSongsFromMediaStore(
                applicationContext = applicationContext,
            )
        }

        viewModel.loadSongs(songs)
    }

    LaunchedEffect(viewState.value.currentMediaItem) {
        mediaItem = viewState.value.currentMediaItem
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
        },
        topBar = {
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (mediaItem != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(300.dp)
                        .aspectRatio(1f)
                ) {
                    AsyncImageImpl(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small),
                        model = mediaItem!!.mediaMetadata.artworkUri!!,
                        contentDescription = "Song cover",
                        contentScale = ContentScale.Fit,
                        isPreview = false
                    )
                }
            } else {
                PlaceholderCreator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(300.dp)
                        .aspectRatio(1f),
                    icon = Icons.Default.MusicNote,
                    colorful = false,
                    contentDescription = "Song cover"
                )
            }
            MediaPlayerPageControlButtons(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                playerUiEvent = viewModel::onUIEvent,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun MediaPlayerPageControlButtons(
    modifier: Modifier = Modifier,
    playerUiEvent: (PlayerUiEvent) -> Unit,
    viewModel: MediaPlayerPageViewModel
) {
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()
    val progress = viewState.value.progress
    val isPlaying = viewState.value.isPlaying
    val duration = viewState.value.duration

    var sliderPosition by remember {
        mutableStateOf<Long?>(null)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(
            value = if (sliderPosition != null) sliderPosition!!.toFloat() else progress,
            valueRange = 0f..(if (duration == C.TIME_UNSET) 0f else duration.toFloat()),
            onValueChange = {
                Log.i("MediaPlayerPage", "onValueChange: $it")
                sliderPosition = it.toLong()
            },
            onValueChangeFinished = {
                playerUiEvent(PlayerUiEvent.UpdateSeekBar((sliderPosition ?: progress).toLong()))
                sliderPosition = null
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { playerUiEvent(PlayerUiEvent.Backward) }) {
                Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Previous song")
            }
            PlayPauseAnimatedButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                isPlaying = isPlaying,
                onClick = { playerUiEvent(PlayerUiEvent.PlayPause) }
            )
            IconButton(onClick = { playerUiEvent(PlayerUiEvent.Forward) }) {
                Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next song")
            }
        }
    }
}
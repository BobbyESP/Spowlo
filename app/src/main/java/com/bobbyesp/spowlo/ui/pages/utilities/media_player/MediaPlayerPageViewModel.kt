package com.bobbyesp.spowlo.ui.pages.utilities.media_player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.bobbyesp.miniplayer_service.service.MediaServiceHandler
import com.bobbyesp.miniplayer_service.service.MediaState
import com.bobbyesp.miniplayer_service.service.PlayerEvent
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.concurrent.formatDuration
import javax.inject.Inject

@HiltViewModel
class MediaPlayerPageViewModel @Inject constructor(
    private val serviceHandler: MediaServiceHandler
) : ViewModel() {

    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            serviceHandler.mediaState.collect { mediaState ->
                when (mediaState) {
                    is MediaState.Buffering -> calculateProgressValues(mediaState.progress)
                    is MediaState.Playing -> mutablePageViewState.update { it.copy(isPlaying = mediaState.isPlaying) }
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

    override fun onCleared() {
        viewModelScope.launch {
            serviceHandler.onPlayerEvent(PlayerEvent.Stop)
        }
        super.onCleared()
    }

    data class PageViewState(
        val uiState: PlayerState = PlayerState.Initial,
        val progress: Float = 0f,
        val progressString: String = "00:00",
        val duration: Long = 0L,
        val isPlaying: Boolean = false,
        val currentMediaItem: MediaItem? = null
    )

    fun onUIEvent(uiEvent: PlayerUiEvent) = viewModelScope.launch {
        when (uiEvent) {
            is PlayerUiEvent.PlayPause -> {
                serviceHandler.onPlayerEvent(PlayerEvent.PlayPause)
            }

            is PlayerUiEvent.Backward -> {
                updateCurrentMediaItem(serviceHandler.getActualMediaItem())
                serviceHandler.onPlayerEvent(PlayerEvent.Previous)
            }

            is PlayerUiEvent.Forward -> {
                updateCurrentMediaItem(serviceHandler.getActualMediaItem())
                serviceHandler.onPlayerEvent(PlayerEvent.Next)
            }

            is PlayerUiEvent.UpdateSeekBar -> {
                calculateProgressValues(uiEvent.newProgress)
                serviceHandler.onPlayerEvent(PlayerEvent.UpdateProgress(uiEvent.newProgress))
            }
        }
    }

    fun loadSong(song: Song) {
        val mediaItem = MediaItem.Builder()
            .setUri(song.path)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .setAlbumTitle(song.album)
                    .build()
            ).build()

        viewModelScope.launch {
            serviceHandler.setMediaItem(mediaItem)
        }
    }

    fun loadSongs(songs: List<Song>) {
        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setUri(song.path)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setAlbumTitle(song.album)
                        .build()
                ).build()
        }

        viewModelScope.launch {
            serviceHandler.createQueue(mediaItems)
        }
    }

    private fun handlePlayerPercent(progress: Float) {
        val progressString =
            formatDuration((progress * mutablePageViewState.value.duration).toLong())
        mutablePageViewState.update {
            it.copy(
                progress = progress, progressString = progressString
            )
        }
    }

    private fun calculateProgressValues(currentProgress: Long) {
        with(pageViewState.value) {
            val progress =
                if (currentProgress > 0) (currentProgress.toFloat() / duration.toFloat()) else 0f
            val progressString = formatDuration(currentProgress)
            mutablePageViewState.update {
                it.copy(
                    progress = progress, progressString = progressString
                )
            }
        }
    }


    private fun calculateProgressValues(currentProgress: Float) {
        with(pageViewState.value) {
            val progress = if (currentProgress > 0) (currentProgress / duration) else 0f
            val progressString = formatDuration((currentProgress * duration).toLong())
            mutablePageViewState.update {
                it.copy(
                    progress = progress, progressString = progressString
                )
            }
        }
    }

    private fun updateCurrentMediaItem(mediaItem: MediaItem?) {
        mutablePageViewState.update {
            it.copy(currentMediaItem = mediaItem)
        }
    }

}

sealed class PlayerUiEvent {
    object PlayPause : PlayerUiEvent()
    object Backward : PlayerUiEvent()
    object Forward : PlayerUiEvent()
    class UpdateSeekBar(val newProgress: Long) : PlayerUiEvent() {
        constructor(newProgress: Float) : this((newProgress * 1000).toLong())
    }
}

sealed class PlayerState {
    object Initial : PlayerState()
    object Ready : PlayerState()
}
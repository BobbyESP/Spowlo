package com.bobbyesp.miniplayer_service.service

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class MediaServiceHandler @Inject constructor(
    private val player: ExoPlayer
) : Player.Listener {
    private val _mediaState = MutableStateFlow<MediaState>(MediaState.Idle)
    val mediaState = _mediaState.asStateFlow()

    private var job: Job? = null

    init {
        player.addListener(this)
        job = Job()
    }

    /**
     * Creates a NEW single-song queue and prepares the player
     */
    fun setMediaItem(mediaItem: MediaItem) {
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    /**
     * Creates NEW a queue of media items and prepares the player
     */
    fun createQueue(mediaItems: List<MediaItem>) {
        player.setMediaItems(mediaItems)
        player.prepare()
    }

    /**
     * Adds a media item to the queue and prepares the player
     */
    fun addToQueue(mediaItem: MediaItem) {
        player.addMediaItem(mediaItem)
        player.prepare()
    }

    suspend fun onPlayerEvent(playerEvent: PlayerEvent){
        when(playerEvent) {
            is PlayerEvent.PlayPause -> {
                if(player.isPlaying) {
                    player.pause()
                    stopProgressUpdate()
                } else {
                    player.play()
                    _mediaState.update {
                        MediaState.Playing(true)
                    }
                    startProgressUpdate()

                }
            }
            is PlayerEvent.Stop -> {
//                player.stop()
                stopProgressUpdate()
            }
            is PlayerEvent.Next -> {
                player.seekToNext()
            }
            is PlayerEvent.Previous -> {
                player.seekToPrevious()
            }
            is PlayerEvent.UpdateProgress -> {
                player.seekTo(playerEvent.updatedProgress)
            }
        }
    }

    fun getActualMediaItem(): MediaItem? {
        return player.currentMediaItem
    }

    fun getActualMediaItemIndex(): Int {
        return player.currentMediaItemIndex
    }

    fun getActualMediaItemMetadata(): MediaMetadata? {
        return player.currentMediaItem?.mediaMetadata
    }

    fun getActualMediaItemDuration(): Long {
        return player.duration
    }

    fun getActualMediaItemPosition(): Long {
        return player.currentPosition
    }

    fun getActualMediaItemPositionPercentage(): Float {
        return player.currentPosition / player.duration.toFloat()
    }


    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> _mediaState.update {
                MediaState.Buffering(player.duration)
            }
            ExoPlayer.STATE_READY -> _mediaState.update {
                MediaState.Ready(player.duration)
            }
            ExoPlayer.STATE_ENDED -> _mediaState.update {
                MediaState.Idle
            }
            ExoPlayer.STATE_IDLE -> _mediaState.update {
                MediaState.Idle
            }
        }
    }

    private suspend fun startProgressUpdate() = job.run {
        while (true) {
            delay(250)
            _mediaState.update {
                MediaState.Progress(player.currentPosition)
            }
        }
    }

    private fun stopProgressUpdate() {
        job?.cancel()
        _mediaState.update {
            MediaState.Playing(false)
        }
    }
}

sealed class PlayerEvent {
    object PlayPause: PlayerEvent()
    object Stop : PlayerEvent()
    object Next : PlayerEvent()
    object Previous : PlayerEvent()
    data class UpdateProgress(val updatedProgress: Long) : PlayerEvent()
}

sealed class MediaState {
    object Idle : MediaState()
    data class Ready(val duration: Long) : MediaState()
    data class Progress(val progress: Long) : MediaState()
    data class Buffering(val progress: Long) : MediaState()
    data class Playing(val isPlaying: Boolean) : MediaState()
}
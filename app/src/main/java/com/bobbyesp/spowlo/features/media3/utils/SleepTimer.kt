package com.bobbyesp.spowlo.features.media3.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class SleepTimer(
    private val scope: CoroutineScope,
    val player: Player,
) : Player.Listener {
    companion object {
        private const val NO_TRIGGER: Long = -1L
    }

    private var sleepTimerJob: Job? = null
    var triggerTime by mutableStateOf(NO_TRIGGER)
        private set
    var pauseWhenSongEnd by mutableStateOf(false)
        private set
    val isActive: Boolean
        get() = triggerTime != NO_TRIGGER || pauseWhenSongEnd

    fun start(minute: Int) {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        if (minute == -1) {
            pauseWhenSongEnd = true
        } else {
            triggerTime = System.currentTimeMillis() + minute.minutes.inWholeMilliseconds
            sleepTimerJob = scope.launch {
                delay(minute.minutes)
                player.pause()
                triggerTime = NO_TRIGGER
            }
        }
    }

    fun clear() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        pauseWhenSongEnd = false
        triggerTime = NO_TRIGGER
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        if (pauseWhenSongEnd) {
            pauseWhenSongEnd = false
            player.pause()
        }
    }

    override fun onPlaybackStateChanged(@Player.State playbackState: Int) {
        if (playbackState == Player.STATE_ENDED && pauseWhenSongEnd) {
            pauseWhenSongEnd = false
            player.pause()
        }
    }
}
@file:UnstableApi package com.bobbyesp.spowlo.features.media3.data.services

import android.net.ConnectivityManager
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.analytics.PlaybackStats
import androidx.media3.exoplayer.analytics.PlaybackStatsListener
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.db.SpowloMusicDatabase
import com.bobbyesp.spowlo.db.entity.LocalMediaMetadata
import com.bobbyesp.spowlo.features.media3.data.queue.EmptyQueue
import com.bobbyesp.spowlo.features.media3.data.queue.Queue
import com.bobbyesp.spowlo.features.media3.utils.DownloadCache
import com.bobbyesp.spowlo.features.media3.utils.PlayerCache
import com.bobbyesp.spowlo.features.media3.utils.SleepTimer
import com.bobbyesp.utilities.audio.model.AudioQuality
import com.bobbyesp.utilities.utilities.preferences.Preferences
import com.bobbyesp.utilities.utilities.preferences.PreferencesKeys.AUDIO_QUALITY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class Media3PlayerService: MediaLibraryService(), Player.Listener, PlaybackStatsListener.Callback  {

    @Inject
    lateinit var db: SpowloMusicDatabase

    private val scope = CoroutineScope(Dispatchers.IO) + Job()

    private val connectivityManager: ConnectivityManager = App.connectivityManager
    private val audioQuality = Preferences.EnumPrefs.getValue(AUDIO_QUALITY, AudioQuality.AUTO)

    private var currentQueue: Queue = EmptyQueue
    var queueTitle: String? = null

    val currentMediaMetadata: MutableStateFlow<LocalMediaMetadata?> = MutableStateFlow(null)

    private val currentSong = currentMediaMetadata.flatMapLatest { mediaMetadata ->
        db.songDao().songFlow(mediaMetadata?.id)
    }.stateIn(scope, SharingStarted.Lazily, null)


    private val currentFormat = currentMediaMetadata.flatMapLatest { mediaMetadata ->
        db.formatDao().formatFlow(mediaMetadata?.id)
    }

    private val normalizeFactor = MutableStateFlow(1f)

    lateinit var sleepTimer: SleepTimer

    @Inject
    @PlayerCache
    lateinit var playerCache: SimpleCache

    @Inject
    @DownloadCache
    lateinit var downloadCache: SimpleCache

    lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaLibrarySession

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        TODO("Not yet implemented")
    }

    override fun onPlaybackStatsReady(
        eventTime: AnalyticsListener.EventTime,
        playbackStats: PlaybackStats
    ) {
        TODO("Not yet implemented")
    }
}
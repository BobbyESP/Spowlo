package com.bobbyesp.spowlo.features.media3.data.services

import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.analytics.PlaybackStats
import androidx.media3.exoplayer.analytics.PlaybackStatsListener
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession

class Media3PlayerService: MediaLibraryService(), Player.Listener, PlaybackStatsListener.Callback  {
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        TODO("Not yet implemented")
    }

    @OptIn(UnstableApi::class)
    override fun onPlaybackStatsReady(
        eventTime: AnalyticsListener.EventTime,
        playbackStats: PlaybackStats
    ) {
        TODO("Not yet implemented")
    }
}
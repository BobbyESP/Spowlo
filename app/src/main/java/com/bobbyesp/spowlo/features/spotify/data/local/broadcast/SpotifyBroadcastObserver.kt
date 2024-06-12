package com.bobbyesp.spowlo.features.spotify.data.local.broadcast

import com.adamratzman.spotify.notifications.SpotifyMetadataChangedData
import com.adamratzman.spotify.notifications.SpotifyPlaybackStateChangedData
import com.adamratzman.spotify.notifications.SpotifyQueueChangedData

/**
 * Interface for classes that want to observe SpotifyBroadcastReceiver
 */
interface SpotifyBroadcastObserver {
    fun onMetadataChanged(data: SpotifyMetadataChangedData)
    fun onPlaybackStateChanged(data: SpotifyPlaybackStateChangedData)
    fun onQueueChanged(data: SpotifyQueueChangedData)
}
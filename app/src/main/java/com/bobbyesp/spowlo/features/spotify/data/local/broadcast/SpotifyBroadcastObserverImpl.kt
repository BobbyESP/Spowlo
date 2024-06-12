package com.bobbyesp.spowlo.features.spotify.data.local.broadcast

import com.adamratzman.spotify.notifications.AbstractSpotifyBroadcastReceiver
import com.adamratzman.spotify.notifications.SpotifyMetadataChangedData
import com.adamratzman.spotify.notifications.SpotifyPlaybackStateChangedData
import com.adamratzman.spotify.notifications.SpotifyQueueChangedData

/**
 * Wrapper for SpotifyBroadcastReceiver
 */
class SpotifyBroadcastObserverImpl : AbstractSpotifyBroadcastReceiver() {
    private val observers: MutableSet<SpotifyBroadcastObserver> = mutableSetOf()

    fun addObserver(observer: SpotifyBroadcastObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: SpotifyBroadcastObserver) {
        observers.remove(observer)
    }

    override fun onMetadataChanged(data: SpotifyMetadataChangedData) {
        observers.forEach {
            it.onMetadataChanged(data)
        }
    }

    override fun onPlaybackStateChanged(data: SpotifyPlaybackStateChangedData) {
        observers.forEach {
            it.onPlaybackStateChanged(data)
        }
    }

    override fun onQueueChanged(data: SpotifyQueueChangedData) {
        observers.forEach {
            it.onQueueChanged(data)
        }
    }
}
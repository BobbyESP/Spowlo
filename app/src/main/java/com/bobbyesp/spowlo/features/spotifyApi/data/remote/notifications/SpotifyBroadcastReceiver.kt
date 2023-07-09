package com.bobbyesp.spowlo.features.spotifyApi.data.remote.notifications

import android.util.Log
import com.adamratzman.spotify.notifications.AbstractSpotifyBroadcastReceiver
import com.adamratzman.spotify.notifications.SpotifyMetadataChangedData
import com.adamratzman.spotify.notifications.SpotifyPlaybackStateChangedData
import com.adamratzman.spotify.notifications.SpotifyQueueChangedData

/**
 * Wrapper for SpotifyBroadcastReceiver
 */
class SpotifyBroadcastReceiver : AbstractSpotifyBroadcastReceiver() {
    private val observers: MutableSet<SpotifyBroadcastObserver> = mutableSetOf()

    fun addObserver(observer: SpotifyBroadcastObserver) {
        Log.i("SpotifyBroadcastReceiver", "addObserver: $observer")
        observers.add(observer)
    }

    fun removeObserver(observer: SpotifyBroadcastObserver) {
        Log.i("SpotifyBroadcastReceiver", "removeObserver: $observer")
        observers.remove(observer)
    }

    override fun onMetadataChanged(data: SpotifyMetadataChangedData) {
        for (observer in observers) {
            observer.onMetadataChanged(data)
        }
    }

    override fun onPlaybackStateChanged(data: SpotifyPlaybackStateChangedData) {
        for (observer in observers) {
            observer.onPlaybackStateChanged(data)
        }
    }

    override fun onQueueChanged(data: SpotifyQueueChangedData) {
        for (observer in observers) {
            observer.onQueueChanged(data)
        }
    }
}

/**
 * Interface for classes that want to observe SpotifyBroadcastReceiver
 */
interface SpotifyBroadcastObserver {
    fun onMetadataChanged(data: SpotifyMetadataChangedData)
    fun onPlaybackStateChanged(data: SpotifyPlaybackStateChangedData)
    fun onQueueChanged(data: SpotifyQueueChangedData)
}
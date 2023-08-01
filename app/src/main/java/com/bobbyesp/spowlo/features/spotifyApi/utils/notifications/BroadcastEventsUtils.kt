package com.bobbyesp.spowlo.features.spotifyApi.utils.notifications

import androidx.compose.runtime.MutableState
import com.adamratzman.spotify.notifications.SpotifyBroadcastEventData
import com.adamratzman.spotify.notifications.SpotifyMetadataChangedData
import com.adamratzman.spotify.notifications.SpotifyPlaybackStateChangedData
import com.adamratzman.spotify.notifications.SpotifyQueueChangedData

fun handleSpotifyBroadcastEvent(data: SpotifyBroadcastEventData) {
    when (data) {
        is SpotifyMetadataChangedData -> {

        }

        is SpotifyQueueChangedData -> {
        }

        is SpotifyPlaybackStateChangedData -> {
        }
    }
}

/**
 * Handle SpotifyBroadcastEvent
 */
fun handleSpotifyBroadcastEvent(
    metadataState: MutableState<SpotifyMetadataChangedData?>,
    queueState: MutableState<SpotifyQueueChangedData?>,
    playbackState: MutableState<SpotifyPlaybackStateChangedData?>,
    event: SpotifyBroadcastEventData
) {
    when (event) {
        is SpotifyMetadataChangedData -> {
            metadataState.value = event
        }

        is SpotifyQueueChangedData -> {
            queueState.value = event
        }

        is SpotifyPlaybackStateChangedData -> {

            playbackState.value = event
        }
    }
}
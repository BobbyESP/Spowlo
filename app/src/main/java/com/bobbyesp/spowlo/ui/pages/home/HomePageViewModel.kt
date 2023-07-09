package com.bobbyesp.spowlo.ui.pages.home

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.adamratzman.spotify.notifications.SpotifyBroadcastEventData
import com.adamratzman.spotify.notifications.SpotifyBroadcastType
import com.adamratzman.spotify.notifications.SpotifyMetadataChangedData
import com.adamratzman.spotify.notifications.SpotifyPlaybackStateChangedData
import com.adamratzman.spotify.notifications.SpotifyQueueChangedData
import com.adamratzman.spotify.notifications.registerSpotifyBroadcastReceiver
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.SpotifyApiRequests
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.notifications.SpotifyBroadcastObserver
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.notifications.SpotifyBroadcastReceiver
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.ActivityCallsShortener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor() : ViewModel(), SpotifyBroadcastObserver {
    private val api = SpotifyApiRequests
    private val activityWrapper = ActivityCallsShortener(MainActivity.getActivity())
    private val spotifyBroadcastReceiver =
        SpotifyBroadcastReceiver()//SpotifyBroadcastReceiver(MainActivity.getActivity())

    init {
        activityWrapper.execute {
            registerSpotifyBroadcastReceiver(
                spotifyBroadcastReceiver,
                *SpotifyBroadcastType.values()
            )
        }
        spotifyBroadcastReceiver.addObserver(this)
    }

    override fun onCleared() {
        super.onCleared()
        spotifyBroadcastReceiver.removeObserver(this)
        Log.i("HomePageViewModel", "onCleared: removed observer")
    }

    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val state: HomePageState = HomePageState.Loading,
        val broadcasts: MutableList<SpotifyBroadcastEventData> = mutableStateListOf(),
        val metadataState: SpotifyMetadataChangedData? = null,
        val playbackState: SpotifyPlaybackStateChangedData? = null,
        val queueState: SpotifyQueueChangedData? = null,
    )

    override fun onMetadataChanged(data: SpotifyMetadataChangedData) {
        mutablePageViewState.update {
            it.copy(
                broadcasts = it.broadcasts.apply { add(data) },
                metadataState = data,
            )
        }
        Log.i("HomePageViewModel", "onMetadataChanged: $data")
    }

    override fun onPlaybackStateChanged(data: SpotifyPlaybackStateChangedData) {
        mutablePageViewState.update {
            it.copy(
                broadcasts = it.broadcasts.apply { add(data) },
                playbackState = data,
            )
        }
        Log.i("HomePageViewModel", "onPlaybackStateChanged: $data")
    }

    override fun onQueueChanged(data: SpotifyQueueChangedData) {
        mutablePageViewState.update {
            it.copy(
                broadcasts = it.broadcasts.apply { add(data) },
                queueState = data,
            )
        }
        Log.i("HomePageViewModel", "onQueueChanged: $data")
    }

}

sealed class HomePageState {
    object Loading : HomePageState()
    object Error : HomePageState()
    object Success : HomePageState()
}
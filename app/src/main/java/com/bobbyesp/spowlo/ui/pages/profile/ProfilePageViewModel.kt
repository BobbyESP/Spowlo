package com.bobbyesp.spowlo.ui.pages.profile

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.adamratzman.spotify.models.SpotifyUserInformation
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.notifications.SpotifyBroadcastEventData
import com.adamratzman.spotify.notifications.SpotifyBroadcastType
import com.adamratzman.spotify.notifications.SpotifyMetadataChangedData
import com.adamratzman.spotify.notifications.SpotifyPlaybackStateChangedData
import com.adamratzman.spotify.notifications.SpotifyQueueChangedData
import com.adamratzman.spotify.notifications.registerSpotifyBroadcastReceiver
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.notifications.SpotifyBroadcastObserver
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.notifications.SpotifyBroadcastReceiver
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.ActivityCallsShortener
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.checkSpotifyApiIsValid
import com.bobbyesp.spowlo.ui.ext.getId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ProfilePageViewModel @Inject constructor() : ViewModel(), SpotifyBroadcastObserver {
    private val TAG = this::class.java.simpleName
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    private val activityWrapper = ActivityCallsShortener(MainActivity.getActivity())
    private val spotifyBroadcastReceiver = SpotifyBroadcastReceiver()

    override fun onCleared() {
        super.onCleared()
        spotifyBroadcastReceiver.removeObserver(this)
    }

    init {
        activityWrapper.execute {
            registerSpotifyBroadcastReceiver(
                spotifyBroadcastReceiver,
                *SpotifyBroadcastType.values()
            )
        }
        spotifyBroadcastReceiver.addObserver(this)
    }

    data class PageViewState(
        val state: ProfilePageState = ProfilePageState.Loading,
        val userInformation: SpotifyUserInformation? = null,
        val actualTrack: Track? = null,
        val broadcasts: MutableList<SpotifyBroadcastEventData> = mutableStateListOf(),
        val metadataState: SpotifyMetadataChangedData? = null,
        val playbackState: SpotifyPlaybackStateChangedData? = null,
        val queueState: SpotifyQueueChangedData? = null,
    )

    suspend fun loadPage() {
        updateState(ProfilePageState.Loading)

        loadUserData()

        updateState(ProfilePageState.Loaded)
    }

    private suspend fun loadUserData() {
        checkSpotifyApiIsValid(MainActivity.getActivity()) { api ->
            val userInformation = api.users.getClientProfile()
            mutablePageViewState.update {
                it.copy(
                    userInformation = userInformation,
                )
            }
        }
    }

    suspend fun searchSongById(id: String) {
        checkSpotifyApiIsValid(MainActivity.getActivity()) { api ->
            val track = api.tracks.getTrack(id)
            mutablePageViewState.update {
                it.copy(
                    actualTrack = track,
                )
            }
        }
    }

    suspend fun sameSongAsBroadcastVerifier() {
        checkSpotifyApiIsValid(MainActivity.getActivity()) { api ->
            val apiPlayingSong = api.player.getCurrentlyPlaying()
            val apiPlayingSongId = apiPlayingSong?.item?.id?.getId()
            Log.i(TAG, "sameSongAsBroadcastVerifier: $apiPlayingSongId")

            val actualSongId = mutablePageViewState.value.metadataState?.playableUri?.id?.getId()
            Log.i(TAG, "sameSongAsBroadcastVerifier: $actualSongId")
            if (apiPlayingSongId == actualSongId) {
                Log.i(TAG, "sameSongAsBroadcastVerifier: Same song, doing nothing")
            } else {
                if (apiPlayingSongId != null) {
                    searchSongById(apiPlayingSongId)
                } else {
                    Log.i(TAG, "sameSongAsBroadcastVerifier: No song playing")
                }
            }

        }
    }

    override fun onMetadataChanged(data: SpotifyMetadataChangedData) {
        mutablePageViewState.update {
            it.copy(
                broadcasts = it.broadcasts.apply { add(data) },
                metadataState = data,
            )
        }
        Log.i(TAG, "onMetadataChanged: $data")
    }

    override fun onPlaybackStateChanged(data: SpotifyPlaybackStateChangedData) {
        mutablePageViewState.update {
            it.copy(
                broadcasts = it.broadcasts.apply { add(data) },
                playbackState = data,
            )
        }
        Log.i(TAG, "onPlaybackStateChanged: $data")
    }

    override fun onQueueChanged(data: SpotifyQueueChangedData) {
        mutablePageViewState.update {
            it.copy(
                broadcasts = it.broadcasts.apply { add(data) },
                queueState = data,
            )
        }
        Log.i(TAG, "onQueueChanged: $data")
    }

    private fun updateState(state: ProfilePageState) {
        mutablePageViewState.update {
            it.copy(state = state)
        }
    }
}

sealed class ProfilePageState {
    object Loading : ProfilePageState()
    object Loaded : ProfilePageState()
    object Error : ProfilePageState()
}

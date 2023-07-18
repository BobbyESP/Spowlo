package com.bobbyesp.spowlo.ui.pages.profile

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.adamratzman.spotify.endpoints.client.ClientPersonalizationApi
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.PlayHistory
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
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.ClientMostListenedArtistsPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.ClientMostListenedSongsPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.ActivityCallsShortener
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.checkSpotifyApiIsValid
import com.bobbyesp.spowlo.ui.ext.getId
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfilePageViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel(), SpotifyBroadcastObserver {
    private val TAG = this::class.java.simpleName
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    private val activityWrapper = ActivityCallsShortener(MainActivity.getActivity())
    private val spotifyBroadcastReceiver = SpotifyBroadcastReceiver()

    override fun onCleared() {
        activityWrapper.execute {
            unregisterReceiver(
                spotifyBroadcastReceiver
            )
        }
        spotifyBroadcastReceiver.removeObserver(this)
        super.onCleared()
    }

    init {
        activityWrapper.execute {
            registerSpotifyBroadcastReceiver(
                spotifyBroadcastReceiver, *SpotifyBroadcastType.values()
            )
        }
        spotifyBroadcastReceiver.addObserver(this)
    }

    data class PageViewState(
        val state: ProfilePageState = ProfilePageState.Loading,
        val isRefreshing: Boolean = false,
        val userInformation: SpotifyUserInformation? = null,
        val actualTrack: Track? = null,
        val mostPlayedArtists: Flow<PagingData<Artist>> = emptyFlow(),
        val mostPlayedSongs: Flow<PagingData<Track>> = emptyFlow(),
        val recentlyPlayedSongs: List<PlayHistory> = emptyList(),
        val broadcasts: MutableList<SpotifyBroadcastEventData> = mutableStateListOf(),
        val metadataState: SpotifyMetadataChangedData? = null,
        val playbackState: SpotifyPlaybackStateChangedData? = null,
        val queueState: SpotifyQueueChangedData? = null,
        val actualTimeRange: ClientPersonalizationApi.TimeRange = ClientPersonalizationApi.TimeRange.ShortTerm,
    )

    suspend fun loadPage(context: Context) {
        updateState(ProfilePageState.Loading)
        runCatching {
                with(context) {
                    loadUserData(this)
                    loadMostListenedArtists(this)
                    loadMostListenedSongs(this)
                    loadRecentlyPlayedSongs(this)
                }
        }.onFailure { e ->
            Log.e(TAG, "loadPage: ", e)
            updateState(ProfilePageState.Error(e))
            return

        }.onSuccess {
            updateState(ProfilePageState.Loaded)
        }
    }

    suspend fun reloadPage(context: Context) {
        updateIsRefreshing(true)
        runCatching {
            with(context) {
                loadMostListenedArtists(this)
                loadMostListenedSongs(this)
                loadRecentlyPlayedSongs(this)
            }
        }.onFailure { e ->
            Log.e(TAG, "reloadPage: ", e)
            updateState(ProfilePageState.Error(e))
            return

        }.onSuccess {
            updateIsRefreshing(false)
        }
    }

    private suspend fun reloadAfterTimeRangeChange(context: Context) {
        try {
            with(context) {
                loadMostListenedArtists(this)
                loadMostListenedSongs(this)
            }
        } catch (e: Exception) {
            Log.e(TAG, "reload: ", e)
            updateState(ProfilePageState.Error(e))
            return
        }
    }

    suspend fun updateTimeRange(timeRange: ClientPersonalizationApi.TimeRange, context: Context) {
        mutablePageViewState.update {
            it.copy(
                actualTimeRange = timeRange,
            )
        }
        reloadAfterTimeRangeChange(context)
    }

    private suspend fun loadUserData(context: Context) {
        checkSpotifyApiIsValid(MainActivity.getActivity(), context) { api ->
            viewModelScope.launch(Dispatchers.IO) {
                val userInformation = api.users.getClientProfile()
                mutablePageViewState.update {
                    it.copy(
                        userInformation = userInformation,
                    )
                }
            }
        }
    }

    suspend fun searchSongById(context: Context, id: String) {
        checkSpotifyApiIsValid(MainActivity.getActivity(), context) { api ->
            viewModelScope.launch(Dispatchers.IO) {
                val track = api.tracks.getTrack(id)
                mutablePageViewState.update {
                    it.copy(
                        actualTrack = track,
                    )
                }
            }
        }
    }

    private suspend fun loadMostListenedArtists(context: Context): Job? {
        return checkSpotifyApiIsValid(MainActivity.getActivity(), context) { api ->
            viewModelScope.launch {
                mutablePageViewState.update {
                    it.copy(mostPlayedArtists = Pager(config = PagingConfig(
                        pageSize = 10,
                        enablePlaceholders = false,
                    ), pagingSourceFactory = {
                        ClientMostListenedArtistsPagingSource(
                            api, timeRange = it.actualTimeRange
                        )
                    }).flow.cachedIn(viewModelScope))
                }
            }

        }
    }

    private suspend fun loadMostListenedSongs(context: Context): Job? {
        return checkSpotifyApiIsValid(MainActivity.getActivity(), context) { api ->
            viewModelScope.launch(Dispatchers.IO) {
                mutablePageViewState.update {
                    it.copy(mostPlayedSongs = Pager(config = PagingConfig(
                        pageSize = 10,
                        enablePlaceholders = false,
                    ), pagingSourceFactory = {
                        ClientMostListenedSongsPagingSource(
                            api, timeRange = it.actualTimeRange
                        )
                    }).flow.cachedIn(viewModelScope))
                }
            }
        }
    }

    private suspend fun loadRecentlyPlayedSongs(context: Context): Job? {
        return checkSpotifyApiIsValid(MainActivity.getActivity(), context) { api ->
            viewModelScope.launch(Dispatchers.IO) {
                mutablePageViewState.update {
                    it.copy(
                        recentlyPlayedSongs = api.player.getRecentlyPlayed(limit = 25).items
                    )
                }
            }
        }
    }

    suspend fun sameSongAsBroadcastVerifier(context: Context) {
        checkSpotifyApiIsValid(MainActivity.getActivity(), context) { api ->
            viewModelScope.launch(Dispatchers.IO) {
                val apiPlayingSong = api.player.getCurrentlyPlaying()
                val apiPlayingSongId = apiPlayingSong?.item?.id?.getId()
                Log.i(
                    TAG,
                    "sameSongAsBroadcastVerifier: Song ID from API request -> $apiPlayingSongId"
                )
                val actualSongId =
                    mutablePageViewState.value.metadataState?.playableUri?.id?.getId()
                Log.i(TAG, "sameSongAsBroadcastVerifier: Song ID from broadcast -> $actualSongId")
                if (apiPlayingSongId == actualSongId) {
                    Log.i(TAG, "sameSongAsBroadcastVerifier: Same song, doing nothing")
                } else {
                    if (apiPlayingSongId != null) {
                        searchSongById(context, apiPlayingSongId)
                    } else {
                        Log.i(TAG, "sameSongAsBroadcastVerifier: No song playing")
                    }
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

    private fun updateIsRefreshing(isRefreshing: Boolean) {
        mutablePageViewState.update {
            it.copy(isRefreshing = isRefreshing)
        }
    }
}

sealed class ProfilePageState {
    object Loading : ProfilePageState()
    object Loaded : ProfilePageState()
    data class Error(val exception: Throwable) : ProfilePageState()
}

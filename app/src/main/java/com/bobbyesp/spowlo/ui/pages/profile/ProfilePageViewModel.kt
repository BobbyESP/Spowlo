package com.bobbyesp.spowlo.ui.pages.profile

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.bobbyesp.spowlo.features.spotifyApi.data.local.notifications.SpotifyBroadcastObserver
import com.bobbyesp.spowlo.features.spotifyApi.data.local.notifications.SpotifyBroadcastReceiver
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.ClientMostListenedArtistsPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.ClientMostListenedSongsPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.ActivityCallsShortener
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.SpotifyAuthManager
import com.bobbyesp.spowlo.ui.ext.getId
import com.bobbyesp.spowlo.ui.ext.toInt
import com.bobbyesp.spowlo.utils.ui.pages.PageStateWithThrowable
import com.t8rin.modalsheet.ModalBottomSheetValue
import com.t8rin.modalsheet.ModalSheetState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
class ProfilePageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val spotifyAuthManager: SpotifyAuthManager
) : ViewModel(), SpotifyBroadcastObserver {

    private val tag = this::class.java.simpleName
    private val clientApi by lazy { spotifyAuthManager.getSpotifyClientApi() }

    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val state: PageStateWithThrowable = PageStateWithThrowable.Loading,
        val sheetState: ModalSheetState = ModalSheetState(initialValue = ModalBottomSheetValue.Hidden),
        val isRefreshing: Boolean = false,
        val userInformation: SpotifyUserInformation? = null,
        val actualTrack: Track? = null,
        val mostPlayedArtists: Flow<PagingData<Artist>> = emptyFlow(),
        val mostPlayedSongs: Flow<PagingData<Track>> = emptyFlow(),
        val recentlyPlayedSongs: List<PlayHistory> = emptyList(),
        val broadcasts: List<SpotifyBroadcastEventData> = emptyList(),
        val metadataState: SpotifyMetadataChangedData? = null,
        val playbackState: SpotifyPlaybackStateChangedData? = null,
        val queueState: SpotifyQueueChangedData? = null,
        val actualTimeRange: ClientPersonalizationApi.TimeRange = ClientPersonalizationApi.TimeRange.ShortTerm
    )


    private val activityWrapper = ActivityCallsShortener(MainActivity.getActivity())
    private val spotifyBroadcastReceiver = SpotifyBroadcastReceiver()

    init {
        activityWrapper.execute {
            registerSpotifyBroadcastReceiver(
                spotifyBroadcastReceiver,
                *SpotifyBroadcastType.values()
            )
        }
        spotifyBroadcastReceiver.addObserver(this)
        loadPage()
    }

    override fun onCleared() {
        activityWrapper.execute {
            unregisterReceiver(spotifyBroadcastReceiver)
        }
        spotifyBroadcastReceiver.removeObserver(this)
        super.onCleared()
    }

    private fun loadPage() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loadUserData()
                loadMostListenedArtists()
                loadMostListenedSongs()
                loadRecentlyPlayedSongs()

                updateState(PageStateWithThrowable.Success)
            } catch (e: Exception) {
                Log.e(tag, "loadPage: ", e)
                updateState(PageStateWithThrowable.Error(e))
            }
        }
    }

    fun reloadPage() {
        updateIsRefreshing(true)
        viewModelScope.launch {
            try {
                if (pageViewState.value.userInformation == null) {
                    loadUserData()
                }
                loadMostListenedArtists()
                loadMostListenedSongs()
            } catch (e: Exception) {
                Log.e(tag, "reloadPage: ", e)
                updateState(PageStateWithThrowable.Error(e))
            } finally {
                updateIsRefreshing(false)
            }
        }
    }

    private fun updateTimeRange(timeRange: Int) {
        val selectedTimeRange = when (timeRange) {
            0 -> ClientPersonalizationApi.TimeRange.ShortTerm
            1 -> ClientPersonalizationApi.TimeRange.MediumTerm
            2 -> ClientPersonalizationApi.TimeRange.LongTerm
            else -> ClientPersonalizationApi.TimeRange.ShortTerm
        }

        mutablePageViewState.update { it.copy(actualTimeRange = selectedTimeRange) }
    }

    fun updateTimeRangeAndReload(timeRange: Int) {
        if (timeRange == pageViewState.value.actualTimeRange.toInt()) {
            return
        }
        updateTimeRange(timeRange)
        reloadAfterTimeRangeChange()
    }

    private fun reloadAfterTimeRangeChange() {
        viewModelScope.launch {
            try {
                loadMostListenedArtists()
                loadMostListenedSongs()
            } catch (e: Exception) {
                Log.e(tag, "reload: ", e)
                // Show error message to the user
                mutablePageViewState.update {
                    it.copy(
                        state = PageStateWithThrowable.Error(e)
                    )
                }
            }
        }
    }

    private suspend fun loadUserData() {
        val userInformation = clientApi?.users?.getClientProfile()
        mutablePageViewState.update { it.copy(userInformation = userInformation) }
    }

    private fun loadMostListenedArtists() {
        val mostPlayedArtists = Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = {
                ClientMostListenedArtistsPagingSource(
                    spotifyApi = clientApi ?: throw Exception("ClientApi is null"),
                    timeRange = pageViewState.value.actualTimeRange
                )
            }
        ).flow.cachedIn(viewModelScope)
        mutablePageViewState.update { it.copy(mostPlayedArtists = mostPlayedArtists) }
    }

    private fun loadMostListenedSongs() {
        val mostPlayedSongs = Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = {
                ClientMostListenedSongsPagingSource(
                    spotifyApi = clientApi ?: throw Exception("ClientApi is null"),
                    timeRange = pageViewState.value.actualTimeRange
                )
            }
        ).flow.cachedIn(viewModelScope)
        mutablePageViewState.update { it.copy(mostPlayedSongs = mostPlayedSongs) }
    }

    private suspend fun loadRecentlyPlayedSongs() {
        val recentlyPlayedSongs = clientApi?.player?.getRecentlyPlayed(limit = 25)?.items
        mutablePageViewState.update {
            it.copy(
                recentlyPlayedSongs = recentlyPlayedSongs ?: emptyList()
            )
        }
    }

    suspend fun sameSongAsBroadcastVerifier() {
        viewModelScope.launch(Dispatchers.IO) {
            val apiPlayingSong = try {
                clientApi?.player?.getCurrentlyPlaying()
            } catch (e: Exception) {
                Log.e(tag, "sameSongAsBroadcastVerifier: ", e)
                null
            }
            val apiPlayingSongId = apiPlayingSong?.item?.id?.getId()
            Log.i(
                tag,
                "sameSongAsBroadcastVerifier: Song ID from API request -> $apiPlayingSongId"
            )
            val actualSongId = pageViewState.value.metadataState?.playableUri?.id?.getId()
            Log.i(tag, "sameSongAsBroadcastVerifier: Song ID from broadcast -> $actualSongId")
            if (apiPlayingSongId == actualSongId) {
                Log.i(tag, "sameSongAsBroadcastVerifier: Same song, doing nothing")
            } else {
                if (apiPlayingSongId != null) {
                    searchSongByIdAndUpdateUi(apiPlayingSongId)
                } else {
                    Log.i(tag, "sameSongAsBroadcastVerifier: No song playing")
                }
            }
        }
    }

    suspend fun searchSongByIdAndUpdateUi(id: String) {
        val track = clientApi?.tracks?.getTrack(id)
        mutablePageViewState.update { it.copy(actualTrack = track) }
    }

    private fun updateState(state: PageStateWithThrowable) =
        mutablePageViewState.update { it.copy(state = state) }

    private fun updateIsRefreshing(refreshing: Boolean) =
        mutablePageViewState.update { it.copy(isRefreshing = refreshing) }

    override fun onMetadataChanged(data: SpotifyMetadataChangedData) {
        mutablePageViewState.update {
            it.copy(
                broadcasts = it.broadcasts.toMutableList().apply { add(data) },
                metadataState = data
            )
        }
        Log.i(tag, "onMetadataChanged: $data")
    }

    override fun onPlaybackStateChanged(data: SpotifyPlaybackStateChangedData) {
        mutablePageViewState.update {
            it.copy(
                broadcasts = it.broadcasts.toMutableList().apply { add(data) },
                playbackState = data
            )
        }
        Log.i(tag, "onPlaybackStateChanged: $data")
    }

    override fun onQueueChanged(data: SpotifyQueueChangedData) {
        mutablePageViewState.update {
            it.copy(
                broadcasts = it.broadcasts.toMutableList().apply { add(data) },
                queueState = data
            )
        }
        Log.i(tag, "onQueueChanged: $data")
    }
}

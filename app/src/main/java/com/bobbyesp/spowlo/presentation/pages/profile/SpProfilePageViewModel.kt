package com.bobbyesp.spowlo.presentation.pages.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.endpoints.client.ClientPersonalizationApi
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.PlayHistory
import com.adamratzman.spotify.models.SpotifyUserInformation
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.notifications.SpotifyBroadcastEventData
import com.adamratzman.spotify.notifications.SpotifyMetadataChangedData
import com.adamratzman.spotify.notifications.SpotifyPlaybackStateChangedData
import com.adamratzman.spotify.notifications.SpotifyQueueChangedData
import com.bobbyesp.spowlo.MainActivity.Companion.spotifyBroadcastManager
import com.bobbyesp.spowlo.features.spotify.auth.CredentialsStorer
import com.bobbyesp.spowlo.features.spotify.data.local.broadcast.SpotifyBroadcastObserver
import com.bobbyesp.spowlo.features.spotify.data.remote.ClientMostListenedArtistsPagingSource
import com.bobbyesp.spowlo.features.spotify.data.remote.ClientMostListenedSongsPagingSource
import com.bobbyesp.utilities.states.NoDataScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpProfilePageViewModel: KoinComponent, ViewModel(), SpotifyBroadcastObserver {
    private val credentialsStore: CredentialsStorer by inject()

    private lateinit var clientApi: SpotifyClientApi

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    init {
        viewModelScope.launch {
            clientApi = credentialsStore.getCredentials().getSpotifyClientPkceApi()
                ?: throw IllegalStateException("Client API is not initialized - Is the user not authenticated?")
            runCatching {
                loadProfileInformation()
            }.onSuccess {
                loadMostListenedSongs()
                loadMostListenedArtists()
            }.onFailure { exception ->
                mutablePageViewState.update {
                    it.copy(
                        state = NoDataScreenState.Error(exception)
                    )
                }
            }
        }
        spotifyBroadcastManager.addObserver(this)
    }

    override fun onCleared() {
        super.onCleared()
        spotifyBroadcastManager.removeObserver(this)
    }

    data class PageViewState(
        val state: NoDataScreenState = NoDataScreenState.Loading,
        val profileInformation: SpotifyUserInformation? = null,
        val userMusicalData: UserMusicalData = UserMusicalData(),
    )

    data class UserMusicalData(
        val mostPlayedArtists: Flow<PagingData<Artist>> = emptyFlow(),
        val mostPlayedSongs: Flow<PagingData<Track>> = emptyFlow(),
        val recentlyPlayedSongs: List<PlayHistory> = emptyList(),
    )

    private val mutableBroadcastsViewState = MutableStateFlow(BroadcastsViewState())
    val broadcastsViewState = mutableBroadcastsViewState.asStateFlow()

    data class BroadcastsViewState(
        val broadcasts: List<SpotifyBroadcastEventData> = emptyList(),
        val metadataState: SpotifyMetadataChangedData? = null,
        val playbackState: SpotifyPlaybackStateChangedData? = null,
        val queueState: SpotifyQueueChangedData? = null,
        val nowPlayingTrack: Track? = null,
    )

    fun reloadProfileInformation() {
        viewModelScope.launch {
            mutablePageViewState.update {
                it.copy(state = NoDataScreenState.Loading)
            }
            loadProfileInformation()
        }
    }

    private suspend fun loadProfileInformation() {
        val userData = viewModelScope.async { clientApi.users.getClientProfile() }.await()
        mutablePageViewState.update {
            it.copy(
                state = NoDataScreenState.Success,
                profileInformation = userData
            )
        }
    }

    private fun loadMostListenedArtists() {
        val mostPlayedArtists = Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = {
                ClientMostListenedArtistsPagingSource(
                    spotifyApi = clientApi,
                    timeRange = ClientPersonalizationApi.TimeRange.ShortTerm
                )
            }
        ).flow.cachedIn(viewModelScope + Dispatchers.IO)
        mutablePageViewState.update {
            it.copy(
                userMusicalData = it.userMusicalData.copy(mostPlayedArtists = mostPlayedArtists)
            )
        }
    }

    private fun loadMostListenedSongs() {
        val mostPlayedSongs = Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = {
                ClientMostListenedSongsPagingSource(
                    spotifyApi = clientApi,
                    timeRange = ClientPersonalizationApi.TimeRange.ShortTerm
                )
            }
        ).flow.cachedIn(viewModelScope + Dispatchers.IO)
        mutablePageViewState.update { it.copy(
            userMusicalData = it.userMusicalData.copy(mostPlayedSongs = mostPlayedSongs)
        ) }
    }

    fun handleBroadcastTrackUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            val nowPlayingApiResponse = clientApi.player.getCurrentlyPlaying()
            val apiSongId = nowPlayingApiResponse?.item?.id
            val broadcastSongId = broadcastsViewState.value.nowPlayingTrack?.id
            if (broadcastSongId == null) {
                if (apiSongId != null) {
                    searchSongAndUpdateUiState(apiSongId)
                }
            } else {
                searchSongAndUpdateUiState(broadcastSongId)
            }
        }
    }

    private suspend fun loadRecentlyPlayedSongs() {
        val recentlyPlayedSongs = viewModelScope.async {
            clientApi.player.getRecentlyPlayed(limit = 25).items
        }
        mutablePageViewState.update {
            it.copy(
                userMusicalData = it.userMusicalData.copy(recentlyPlayedSongs = recentlyPlayedSongs.await())
            )
        }
    }

    private suspend fun searchSongById(id: String): Track? {
        return viewModelScope.async(Dispatchers.IO) { clientApi.tracks.getTrack(id) }.await()
    }

    private suspend fun searchSongAndUpdateUiState(id: String) {
        val song = searchSongById(id)
        mutableBroadcastsViewState.update {
            it.copy(nowPlayingTrack = song)
        }
    }

    override fun onMetadataChanged(data: SpotifyMetadataChangedData) {
        mutableBroadcastsViewState.update {
            it.copy(
                broadcasts = it.broadcasts.toMutableList().apply { add(data) },
                metadataState = data
            )
        }
        Log.i("SpProfilePageViewModel", "onMetadataChanged: $data")
    }

    override fun onPlaybackStateChanged(data: SpotifyPlaybackStateChangedData) {
        mutableBroadcastsViewState.update {
            it.copy(
                broadcasts = it.broadcasts.toMutableList().apply { add(data) },
                playbackState = data
            )
        }
        Log.i("SpProfilePageViewModel", "onPlaybackStateChanged: $data")
    }

    override fun onQueueChanged(data: SpotifyQueueChangedData) {
        mutableBroadcastsViewState.update {
            it.copy(
                broadcasts = it.broadcasts.toMutableList().apply { add(data) },
                queueState = data
            )
        }
        Log.i("SpProfilePageViewModel", "onQueueChanged: $data")
    }

    sealed class UiEvent {

    }
}
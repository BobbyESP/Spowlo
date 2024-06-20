package com.bobbyesp.spowlo.presentation.pages.spotify.profile

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
import com.adamratzman.spotify.notifications.SpotifyMetadataChangedData
import com.adamratzman.spotify.notifications.SpotifyPlaybackStateChangedData
import com.adamratzman.spotify.notifications.SpotifyQueueChangedData
import com.bobbyesp.spowlo.MainActivity.Companion.spotifyBroadcastManager
import com.bobbyesp.spowlo.features.spotify.auth.CredentialsStorer
import com.bobbyesp.spowlo.features.spotify.data.local.broadcast.SpotifyBroadcastObserver
import com.bobbyesp.spowlo.features.spotify.data.remote.ClientMostListenedArtistsPagingSource
import com.bobbyesp.spowlo.features.spotify.data.remote.ClientMostListenedSongsPagingSource
import com.bobbyesp.utilities.states.NoDataScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
class SpProfilePageViewModel @Inject constructor(
    private val credentialsStore: CredentialsStorer
) : ViewModel(), SpotifyBroadcastObserver {
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

    override fun onMetadataChanged(data: SpotifyMetadataChangedData) {
        Log.i("SpotifyBroadcastObserver", "Metadata changed: $data")
    }

    override fun onPlaybackStateChanged(data: SpotifyPlaybackStateChangedData) {
        Log.i("SpotifyBroadcastObserver", "Playback state changed: $data")
    }

    override fun onQueueChanged(data: SpotifyQueueChangedData) {
        Log.i("SpotifyBroadcastObserver", "Queue changed: $data")
    }

    sealed class UiEvent {

    }
}
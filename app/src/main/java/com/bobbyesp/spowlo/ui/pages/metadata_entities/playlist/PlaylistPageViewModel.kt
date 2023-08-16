package com.bobbyesp.spowlo.ui.pages.metadata_entities.playlist

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.adamratzman.spotify.models.Playlist
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.SpotifyApiRequests
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.ClientPlaylistTracksAsTracksPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.sp_app.PlaylistTracksAsTracksPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.utils.createPager
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.SpotifyAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class PlaylistPageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authManager: SpotifyAuthManager
) : ViewModel() {
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val state: PlaylistPageState = PlaylistPageState.Loading,
        val playlistTracksPaginated: Flow<PagingData<Track>> = emptyFlow(),
        val trackForSheet: Track? = null,
        val dominantColor: Color? = null,
    )

    suspend fun loadPlaylist(id: String) {
        try {
            val api = authManager.getSpotifyClientApi() ?: SpotifyApiRequests.provideSpotifyApi()
            if (pageViewState.value.state != PlaylistPageState.Loading) updateState(
                PlaylistPageState.Loading
            )
            viewModelScope.launch(Dispatchers.IO) {
                val playlistDeferred = withContext(Dispatchers.IO) {
                    async { api.playlists.getPlaylist(id) }
                }
                val playlist = playlistDeferred.await()
                    ?: throw Exception(context.getString(R.string.playlist_not_found))

                updateState(PlaylistPageState.Success(playlist))
                getPlaylistTracksPaginated(id)
            }
        } catch (e: Exception) {
            updateState(PlaylistPageState.Error(e.message ?: "Unknown error"))
        }
    }

    private suspend fun getPlaylistTracksPaginated(id: String) {
        val playlistTracksPager = createPager(
            clientApi = authManager.getSpotifyClientApi(),
            isLogged = authManager.isAuthenticated(),
            pagingSourceFactory = { clientApi ->
                ClientPlaylistTracksAsTracksPagingSource(
                    spotifyApi = clientApi,
                    playlistId = id
                )
            },
            nonLoggedSourceFactory = { PlaylistTracksAsTracksPagingSource(playlistId = id) },
            coroutineScope = viewModelScope,
        )

        mutablePageViewState.update {
            it.copy(
                playlistTracksPaginated = playlistTracksPager!!
            )
        }
    }

    fun selectTrackForSheet(track: Track) {
        mutablePageViewState.update {
            it.copy(
                trackForSheet = track
            )
        }
    }

    private fun updateState(state: PlaylistPageState) {
        mutablePageViewState.update { it.copy(state = state) }
    }

    companion object {
        sealed class PlaylistPageState {
            data object Loading : PlaylistPageState()
            data class Error(val e: String) : PlaylistPageState()
            data class Success(val playlist: Playlist) : PlaylistPageState()
        }
    }
}
package com.bobbyesp.spowlo.ui.pages.searcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.models.SimplePlaylist
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.features.spotify_api.data.paging.SimpleAlbumPagingSource
import com.bobbyesp.spowlo.features.spotify_api.data.paging.SimplePlaylistPagingSource
import com.bobbyesp.spowlo.features.spotify_api.data.paging.TrackPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearcherPageViewModel @Inject constructor() : ViewModel() {

    private var searchJob: Job? = null

    private val mutableViewStateFlow = MutableStateFlow(ViewState())
    val viewStateFlow = mutableViewStateFlow.asStateFlow()

    data class ViewState(
        val query: String = "",
        val viewState: ViewSearchState = ViewSearchState.Idle,
        val activeSearchType: SpotifySearchType = SpotifySearchType.TRACK,
        val searchedTracks: Flow<PagingData<Track>> = emptyFlow(),
        val searchedAlbums: Flow<PagingData<SimpleAlbum>> = emptyFlow(),
        val searchedPlaylists: Flow<PagingData<SimplePlaylist>> = emptyFlow(),
    )

    private fun chooseSearchType(spotifyItemType: SpotifySearchType) {
        val actualFilter = viewStateFlow.value.activeSearchType
        if (actualFilter == spotifyItemType) {
            return
        } else {
            mutableViewStateFlow.update {
                it.copy(
                    activeSearchType = spotifyItemType
                )
            }
        }
    }

    fun chooseSearchTypeAndSearch(searchType: SpotifySearchType) {
        chooseSearchType(searchType)
        viewModelScope.launch(Dispatchers.IO) {
            if (viewStateFlow.value.query.isNotEmpty()) search(
                searchType = searchType
            )
        }
    }

    fun updateSearchText(text: String) {
        mutableViewStateFlow.update {
            it.copy(query = text)
        }
    }

    suspend fun search(
        searchType: SpotifySearchType = viewStateFlow.value.activeSearchType,
    ) {
        val query = viewStateFlow.value.query
        searchJob?.cancel()
        updateViewState(ViewSearchState.Loading)
        searchJob = viewModelScope.launch {
            try {
                when (searchType) {
                    SpotifySearchType.TRACK -> {
                        getTracksPaginatedData(query = query)
                    }

                    SpotifySearchType.ALBUM -> {
                        getAlbumsPaginatedData(query = query)
                    }

                    SpotifySearchType.PLAYLIST -> {
                        getSimplePaginatedData(query = query)
                    }
                }

                updateViewState(ViewSearchState.Success)
            } catch (e: Exception) {
                updateViewState(ViewSearchState.Error(e.localizedMessage ?: "Error"))
            }
        }
    }

    //********************* CLIENT ***********************//
    private fun getTracksPaginatedData(query: String) {
        val tracksPager = Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 40,
            ),
            pagingSourceFactory = {
                TrackPagingSource(
                    spotifyApi = null,
                    query = query,
                )
            }
        ).flow.cachedIn(viewModelScope)
        mutableViewStateFlow.update {
            it.copy(
                searchedTracks = tracksPager
            )
        }
    }

    private fun getAlbumsPaginatedData(query: String) {
        val albumsPager = Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 40,
            ),
            pagingSourceFactory = {
                SimpleAlbumPagingSource(
                    spotifyApi = null,
                    query = query,
                )
            },
        ).flow.cachedIn(viewModelScope)
        mutableViewStateFlow.update {
            it.copy(
                searchedAlbums = albumsPager
            )
        }
    }

    private fun getSimplePaginatedData(query: String) {
        val playlistsPager = Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 40,
            ),
            pagingSourceFactory = {
                SimplePlaylistPagingSource(
                    spotifyApi = null,
                    query = query,
                )
            },
        ).flow.cachedIn(viewModelScope)
        mutableViewStateFlow.update {
            it.copy(
                searchedPlaylists = playlistsPager
            )
        }
    }

    private fun updateViewState(searchViewState: ViewSearchState) {
        mutableViewStateFlow.update {
            it.copy(
                viewState = searchViewState,
            )
        }
    }

}

//create the possible states of the view
sealed class ViewSearchState {
    data object Idle : ViewSearchState()
    data object Loading : ViewSearchState()
    data object Success : ViewSearchState()
    data class Error(val error: String) : ViewSearchState()
}

enum class SpotifySearchType {
    TRACK,
    ALBUM,
    PLAYLIST;

    fun asString(): String {
        return when (this) {
            TRACK -> "track"
            ALBUM -> "album"
            PLAYLIST -> "playlist"
        }
    }

    fun String.asSpotifySearchType(): SpotifySearchType {
        return when (this) {
            "track" -> TRACK
            "album" -> ALBUM
            "playlist" -> PLAYLIST
            else -> TRACK
        }
    }
}
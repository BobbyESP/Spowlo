package com.bobbyesp.spowlo.ui.pages.search

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.models.SimplePlaylist
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.ArtistsClientPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.SimpleAlbumsClientPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.SimplePlaylistsClientPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.TracksClientPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.sp_app.ArtistsPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.sp_app.SimpleAlbumPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.sp_app.SimplePlaylistPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.sp_app.TrackPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.utils.createPager
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.SpotifyAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SearchViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val spotifyAuthManager: SpotifyAuthManager
) : ViewModel() {
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    private var searchJob: Job? = null

    private val clientApi by lazy {
        spotifyAuthManager.getSpotifyClientApi()
    }

    data class PageViewState(
        val searchViewState: SearchViewState = SearchViewState.Idle,
        val query: String = "",
        val activeSearchType: SpotifyItemType = SpotifyItemType.TRACKS,
        val searchedTracks: Flow<PagingData<Track>> = emptyFlow(),
        val searchedAlbums: Flow<PagingData<SimpleAlbum>> = emptyFlow(),
        val searchedArtists: Flow<PagingData<Artist>> = emptyFlow(),
        val searchedPlaylists: Flow<PagingData<SimplePlaylist>> = emptyFlow(),
    )

    private fun chooseSearchType(spotifyItemType: SpotifyItemType) {
        val actualFilter = pageViewState.value.activeSearchType
        if (actualFilter == spotifyItemType) {
            return
        } else {
            mutablePageViewState.update {
                it.copy(
                    activeSearchType = spotifyItemType
                )
            }
        }
    }

    fun chooseSearchTypeAndSearch(searchType: SpotifyItemType) {
        chooseSearchType(searchType)
        viewModelScope.launch {
            if (pageViewState.value.query.isNotEmpty()) search(
                searchType = searchType
            )
        }
    }

    suspend fun search(
        searchType: SpotifyItemType = pageViewState.value.activeSearchType,
    ) {
        val query = pageViewState.value.query
        searchJob?.cancel()
        updateViewState(SearchViewState.Loading)
        searchJob = viewModelScope.launch {
            try {
                when (searchType) {
                    SpotifyItemType.TRACKS -> {
                        Log.i("SearchViewModel", "search: $query")
                        getTracksPaginatedData(query = query)
                    }

                    SpotifyItemType.ALBUMS -> {
                        getAlbumsPaginatedData(query = query)
                    }

                    SpotifyItemType.ARTISTS -> {
                        getArtistsPaginatedData(query = query)
                    }

                    SpotifyItemType.PLAYLISTS -> {
                        getSimplePaginatedData(query = query)
                    }
                }

                updateViewState(SearchViewState.Success)
            } catch (e: Exception) {
                updateViewState(SearchViewState.Error(e))
            }
        }
    }

    //********************* CLIENT ***********************//
    private suspend fun getTracksPaginatedData(query: String) {
        val tracksPager = createPager(
            clientApi = clientApi,
            isLogged = spotifyAuthManager.isAuthenticated(),
            pagingSourceFactory = { api ->
                TracksClientPagingSource(
                    spotifyApi = api,
                    query = query,
                )
            },
            nonLoggedSourceFactory = {
                TrackPagingSource(
                    spotifyApi = null,
                    query = query,
                )
            },
            coroutineScope = viewModelScope,

        )
        mutablePageViewState.update {
            it.copy(
                searchedTracks = tracksPager!!
            )
        }
    }

    private suspend fun getAlbumsPaginatedData(query: String) {
        val albumsPager = createPager(
            clientApi = clientApi,
            isLogged = spotifyAuthManager.isAuthenticated(),
            pagingSourceFactory = { api ->
                SimpleAlbumsClientPagingSource(
                    spotifyApi = api,
                    query = query,
                )
            },
            nonLoggedSourceFactory = {
                SimpleAlbumPagingSource(
                    spotifyApi = null,
                    query = query,
                )
            },
            coroutineScope = viewModelScope,
        )
        mutablePageViewState.update {
            it.copy(
                searchedAlbums = albumsPager!!
            )
        }
    }

    private suspend fun getSimplePaginatedData(query: String) {
        val playlistsPager = createPager(
            clientApi = clientApi,
            isLogged = spotifyAuthManager.isAuthenticated(),
            pagingSourceFactory = { api ->
                SimplePlaylistsClientPagingSource(
                    spotifyApi = api,
                    query = query,
                )
            },
            nonLoggedSourceFactory = {
                SimplePlaylistPagingSource(
                    spotifyApi = null,
                    query = query,
                )
            },
            coroutineScope = viewModelScope,
        )
        mutablePageViewState.update {
            it.copy(
                searchedPlaylists = playlistsPager!!
            )
        }
    }

    private suspend fun getArtistsPaginatedData(query: String) {
        val artistsPager = createPager(
            clientApi = clientApi,
            isLogged = spotifyAuthManager.isAuthenticated(),
            pagingSourceFactory = { api ->
                ArtistsClientPagingSource(
                    spotifyApi = api,
                    query = query,
                )
            },
            nonLoggedSourceFactory = {
                ArtistsPagingSource(
                    spotifyApi = null,
                    query = query,
                )
            },
            coroutineScope = viewModelScope,
        )
        mutablePageViewState.update {
            it.copy(
                searchedArtists = artistsPager!!
            )
        }
    }

    private fun updateViewState(searchViewState: SearchViewState) {
        mutablePageViewState.update {
            it.copy(
                searchViewState = searchViewState,
            )
        }
    }

    fun updateQuery(query: String) {
        mutablePageViewState.update {
            it.copy(
                query = query,
            )
        }
    }

    companion object {
        sealed class SearchViewState {
            object Idle : SearchViewState()
            object Loading : SearchViewState()
            object Success : SearchViewState()
            data class Error(val error: Exception) : SearchViewState()
        }
    }
}
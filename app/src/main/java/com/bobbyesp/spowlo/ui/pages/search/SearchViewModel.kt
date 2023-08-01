package com.bobbyesp.spowlo.ui.pages.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.models.SimplePlaylist
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.SearchArtistsClientPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.SearchSimpleAlbumsClientPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.SearchSimplePlaylistsClientPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.SearchTracksClientPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.sp_app.SimpleAlbumPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.sp_app.TrackPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.utils.createPager
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.checkSpotifyApiIsValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    private var searchJob: Job? = null

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
            mutablePageViewState.value =
                pageViewState.value.copy(activeSearchType = spotifyItemType)
        }
    }

    fun chooseSearchTypeAndSearch(searchType: SpotifyItemType, context: Context) {
        chooseSearchType(searchType)
        viewModelScope.launch {
            if (pageViewState.value.query.isNotEmpty()) search(
                searchType = searchType,
                context = context
            )
        }
    }

    suspend fun search(
        searchType: SpotifyItemType = pageViewState.value.activeSearchType,
        context: Context
    ) {
        val query = pageViewState.value.query
        searchJob?.cancel()
        updateViewState(SearchViewState.Loading)
        searchJob = viewModelScope.launch {
            try {
                when (searchType) {
                    SpotifyItemType.TRACKS -> {
                        Log.i("SearchViewModel", "search: $query")
                        getTracksPaginatedData(context = context, query = query)
                    }

                    SpotifyItemType.ALBUMS -> {
                        getAlbumsPaginatedData(context = context, query = query)
                    }

                    SpotifyItemType.ARTISTS -> {
                        getArtistsPaginatedData(context = context, query = query)
                    }

                    SpotifyItemType.PLAYLISTS -> {
                        getSimplePaginatedData(context = context, query = query)
                    }
                }

                updateViewState(SearchViewState.Success)
            } catch (e: Exception) {
                updateViewState(SearchViewState.Error(e))
            }
        }
    }

    //********************* CLIENT ***********************//
    private suspend fun getTracksPaginatedData(context: Context, query: String) {
        val tracksPager = createPager(
            context = context,
            pagingSourceFactory = { api ->
                SearchTracksClientPagingSource(
                    spotifyApi = api,
                    query = query,
                )
            },
            authFailedPagingSource = {
                TrackPagingSource(
                    spotifyApi = null,
                    query = query,
                )
            },
            coroutineScope = viewModelScope
        )
        mutablePageViewState.update {
            it.copy(
                searchedTracks = tracksPager!!
            )
        }
    }

    private suspend fun getAlbumsPaginatedData(context: Context, query: String) {
        try {
            checkSpotifyApiIsValid(applicationContext = context) { api ->
                mutablePageViewState.update {
                    it.copy(
                        searchedAlbums = Pager(
                            config = PagingConfig(
                                pageSize = 20,
                                enablePlaceholders = false,
                                initialLoadSize = 40,
                            ),
                            pagingSourceFactory = {
                                SearchSimpleAlbumsClientPagingSource(
                                    spotifyApi = api,
                                    query = query,
                                )
                            }
                        ).flow.cachedIn(viewModelScope)
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("SearchViewModel", "getAlbumsPaginatedData: ${e.message}")

            try {
                mutablePageViewState.update {
                    it.copy(
                        searchedAlbums = Pager(
                            config = PagingConfig(
                                pageSize = 20,
                                enablePlaceholders = false,
                                initialLoadSize = 40,
                            ),
                            pagingSourceFactory = {
                                SimpleAlbumPagingSource(
                                    query = query,
                                )
                            }
                        ).flow.cachedIn(viewModelScope)
                    )
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", "getAlbumsPaginatedData: ${e.message}")
            }
        }
    }

    private suspend fun getSimplePaginatedData(context: Context, query: String) {
        checkSpotifyApiIsValid(applicationContext = context) { api ->
            mutablePageViewState.update {
                it.copy(
                    searchedPlaylists = Pager(
                        config = PagingConfig(
                            pageSize = 20,
                            enablePlaceholders = false,
                            initialLoadSize = 40,
                        ),
                        pagingSourceFactory = {
                            SearchSimplePlaylistsClientPagingSource(
                                spotifyApi = api,
                                query = query,
                            )
                        }
                    ).flow.cachedIn(viewModelScope)
                )
            }
        }
    }

    private suspend fun getArtistsPaginatedData(context: Context, query: String) {
        checkSpotifyApiIsValid(applicationContext = context) { api ->
            mutablePageViewState.update {
                it.copy(
                    searchedArtists = Pager(
                        config = PagingConfig(
                            pageSize = 20,
                            enablePlaceholders = false,
                            initialLoadSize = 40,
                        ),
                        pagingSourceFactory = {
                            SearchArtistsClientPagingSource(
                                spotifyApi = api,
                                query = query,
                            )
                        }
                    ).flow.cachedIn(viewModelScope)
                )
            }
        }
    }

    //********************* SPOTIFY APP SEARCH ***********************//


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
}

sealed class SearchViewState {
    object Idle : SearchViewState()
    object Loading : SearchViewState()
    object Success : SearchViewState()
    data class Error(val error: Exception) : SearchViewState()
}
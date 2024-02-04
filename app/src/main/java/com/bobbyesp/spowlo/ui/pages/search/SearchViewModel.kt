package com.bobbyesp.spowlo.ui.pages.search

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.models.SimplePlaylist
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.data.local.db.searching.SearchingHistoryDatabase
import com.bobbyesp.spowlo.data.local.db.searching.entity.SpotifySearchEntity
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.ArtistsClientPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.SimpleAlbumsClientPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.SimplePlaylistsClientPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client.TracksClientPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.sp_app.ArtistsPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.sp_app.SimpleAlbumPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.sp_app.SimplePlaylistPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.sp_app.TrackPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.utils.createPager
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.SpotifyAuthManager
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
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SearchViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val spotifyAuthManager: SpotifyAuthManager,
    private val searchDb: SearchingHistoryDatabase,
) : ViewModel() {
    private val isLoggedIn = runBlocking(Dispatchers.IO) { spotifyAuthManager.isAuthenticated() }
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    private var searchJob: Job? = null

    private lateinit var clientApi: SpotifyClientApi
    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadDbHistory()
            clientApi = spotifyAuthManager.getSpotifyClientApi() ?: throw IllegalStateException("ClientApi is null")
        }
    }

    data class PageViewState(
        val searchViewState: SearchViewState = SearchViewState.Idle,
        val query: String = "",
        val activeSearchType: SpotifyItemType = SpotifyItemType.TRACKS,
        val searchedTracks: Flow<PagingData<Track>>? = null,
        val searchedAlbums: Flow<PagingData<SimpleAlbum>>? = null,
        val searchedArtists: Flow<PagingData<Artist>>? = null,
        val searchedPlaylists: Flow<PagingData<SimplePlaylist>>? = null,
        val dbSearchHistory: Flow<List<SpotifySearchEntity>> = emptyFlow(),
    )

    private fun loadDbHistory() {
        val dbHistory = searchDb.spotifySearchingDao().getAllFlow()
        mutablePageViewState.update {
            it.copy(
                dbSearchHistory = dbHistory
            )
        }
    }

    fun deleteFromDbHistory(searchId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                searchDb.spotifySearchingDao().deleteById(searchId)
            }.onFailure {
                //TODO
            }.onSuccess {
                //TODO
            }
        }
    }

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

    fun chooseSearchTypeAndSearch(searchType: SpotifyItemType, addToDb: Boolean = true) {
        chooseSearchType(searchType)
        viewModelScope.launch {
            if (pageViewState.value.query.isNotEmpty()) search(
                searchType = searchType,
                addToDb = addToDb
            )
        }
    }

    suspend fun search(
        query: String = pageViewState.value.query,
        searchType: SpotifyItemType = pageViewState.value.activeSearchType,
        addToDb: Boolean = true
    ) {
        searchJob?.cancel()
        updateViewState(SearchViewState.Loading)
        searchJob = viewModelScope.launch {
            runCatching {
                when (searchType) {
                    SpotifyItemType.TRACKS -> {
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
            }.onSuccess {
                if (query.isNotEmpty() && addToDb) {
                    searchDb.spotifySearchingDao().insert(
                        SpotifySearchEntity(
                            id = 0,
                            search = query,
                            type = searchType,
                        )
                    )
                }
                updateViewState(SearchViewState.Success)
            }.onFailure {
                updateViewState(SearchViewState.Error(it))
            }
        }
    }

    //********************* CLIENT ***********************//
    private fun getTracksPaginatedData(query: String) {
        val tracksPager = createPager(
            clientApi = clientApi,
            isLogged = isLoggedIn,
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
                searchedTracks = tracksPager
            )
        }
    }

    private fun getAlbumsPaginatedData(query: String) {
        val albumsPager = createPager(
            clientApi = clientApi,
            isLogged = isLoggedIn,
            pagingSourceFactory = { api ->
                SimpleAlbumsClientPagingSource(
                    spotifyApi = api, query = query
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
                searchedAlbums = albumsPager
            )
        }
    }

    private fun getSimplePaginatedData(query: String) {
        val playlistsPager = createPager(
            clientApi = clientApi,
            isLogged = isLoggedIn,
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
                searchedPlaylists = playlistsPager
            )
        }
    }

    private fun getArtistsPaginatedData(query: String) {
        val artistsPager = createPager(
            clientApi = clientApi,
            isLogged = isLoggedIn,
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

        if (artistsPager == null) {
            updateViewState(SearchViewState.Error(Exception("artistsPager is null")))
        } else {
            mutablePageViewState.update {
                it.copy(
                    searchedArtists = artistsPager
                )
            }
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
            data object Idle : SearchViewState()
            data object Loading : SearchViewState()
            data object Success : SearchViewState()
            data class Error(val error: Throwable) : SearchViewState()
        }
    }
}
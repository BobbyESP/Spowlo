package com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.selected

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.adamratzman.spotify.utils.Market
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.toSongs
import com.bobbyesp.spowlo.features.lyrics_downloader.data.remote.SpotifyLyricService
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.SpotifyApiRequests
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.TrackAsSongPagingSource
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.searching.TrackSearch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SelectedSongLyricsPageViewModel @Inject constructor(
    private val lyricsApi: SpotifyLyricService
) : ViewModel() {

    private val api = SpotifyApiRequests

    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val state: SelectedSongLyricsPageState = SelectedSongLyricsPageState.Loading,
        val pageStage: PageStage = PageStage.Selecting,
        val tracks: Flow<PagingData<Song>> = emptyFlow(),
        val selectedSong: Song? = null
    )

    fun selectSong(song: Song) {
        mutablePageViewState.update {
            it.copy(selectedSong = song, pageStage = PageStage.Selected)
        }
    }

    fun clearSelectedSong() {
        mutablePageViewState.update {
            it.copy(
                selectedSong = null,
                pageStage = PageStage.Selecting,
                state = SelectedSongLyricsPageState.Loading
            )
        }
    }

    suspend fun searchSongOnSpotify(query: String): List<Song> {
        val songs = withContext(Dispatchers.IO) {
            TrackSearch(api.provideSpotifyApi()).search(query).toSongs()
        }

        return songs
    }

    fun getTrackPagingData(query: String, market: Market?) {
        mutablePageViewState.update {
            it.copy(
                tracks = Pager(
                    config = PagingConfig(pageSize = 25, enablePlaceholders = false),
                    pagingSourceFactory = { TrackAsSongPagingSource(null, query, market) }
                ).flow.cachedIn(viewModelScope)
            )
        }

    }

    suspend fun getLyrics(songUrl: String) {

        updateState(SelectedSongLyricsPageState.Loading)

        val lyrics = withContext(Dispatchers.IO) {
            lyricsApi.getSyncedLyricsAsString(songUrl)
        }

        if (lyrics.isEmpty()) {
            updateState(SelectedSongLyricsPageState.Error("No lyrics found"))
            return
        } else {
            updateState(SelectedSongLyricsPageState.Loaded(lyrics))
        }
    }

    private fun updatePageStage(pageStage: PageStage) {
        mutablePageViewState.update {
            it.copy(pageStage = pageStage)
        }
    }

    private fun updateState(state: SelectedSongLyricsPageState) {
        mutablePageViewState.update {
            it.copy(state = state)
        }
    }
}

sealed class SelectedSongLyricsPageState {
    object Loading : SelectedSongLyricsPageState()
    data class Loaded(val lyrics: String) : SelectedSongLyricsPageState()
    data class Error(val error: String) : SelectedSongLyricsPageState()
}

sealed class PageStage {
    object Selecting : PageStage()
    object Selected : PageStage()

}
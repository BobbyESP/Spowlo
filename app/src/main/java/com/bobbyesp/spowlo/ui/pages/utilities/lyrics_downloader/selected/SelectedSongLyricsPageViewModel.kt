package com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.selected

import androidx.lifecycle.ViewModel
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.toSongs
import com.bobbyesp.spowlo.features.lyrics_downloader.data.remote.SpotifyLyricService
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.SpotifyApiRequests
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.searching.TrackSearch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        val selectedSong: Song? = null
    )

    fun selectSong(song: Song) {
        mutablePageViewState.update {
            it.copy(selectedSong = song, pageStage = PageStage.Selected)
        }
    }

    fun clearSelectedSong() {
        mutablePageViewState.update {
            it.copy(selectedSong = null, pageStage = PageStage.Selecting, state = SelectedSongLyricsPageState.Loading)
        }
    }
    suspend fun searchSongOnSpotify(query: String): List<Song> {
        val songs = withContext(Dispatchers.IO) {
            TrackSearch(api.provideSpotifyApi()).search(query).toSongs()
        }

        return songs
    }

    suspend fun getLyrics(songUrl: String) {

        updateState(SelectedSongLyricsPageState.Loading)

        val lyrics = withContext(Dispatchers.IO) {
            lyricsApi.getSyncedLyricsAsString(songUrl)
        }

        updateState(SelectedSongLyricsPageState.Loaded(lyrics))
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
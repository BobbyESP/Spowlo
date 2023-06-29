package com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader

import androidx.lifecycle.ViewModel
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.MediaStoreReceiver
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.SpotifyApiRequests
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LyricsDownloaderPageViewModel @Inject constructor() : ViewModel() {
    private val TAG = "LyricsDownloaderPageViewModel"
    private val api = SpotifyApiRequests

    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val state: LyricsDownloaderPageState = LyricsDownloaderPageState.Loading,
    )
    /**
     * Loads all songs from the media store db
     * @return a list of songs
     */
    suspend fun loadMediaStoreTracks(): List<Song> {

        updateState(LyricsDownloaderPageState.Loading)

        val songs = withContext(Dispatchers.IO) {
            MediaStoreReceiver.getAllSongsFromMediaStore(
                applicationContext = App.context,
            )
        }

        updateState(LyricsDownloaderPageState.Loaded(songs))

        return songs
    }

    /**
     * Updates the state of the page
     * @param state the new state
     */
    private fun updateState(state: LyricsDownloaderPageState) {
        mutablePageViewState.update {
            it.copy(state = state)
        }
    }

}

/**
 * The state of the page
 */
sealed class LyricsDownloaderPageState {
    object Loading : LyricsDownloaderPageState()
    class Loaded(val songs: List<Song>) : LyricsDownloaderPageState()
    object Error : LyricsDownloaderPageState()
}
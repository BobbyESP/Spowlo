package com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.bobbyesp.spowlo.data.local.db.searching.entity.SearchEntity
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.MediaStoreFilterType
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.MediaStoreReceiver
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import com.bobbyesp.spowlo.utils.databases.SearchingDbHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LyricsDownloaderPageViewModel @Inject constructor(
    private val searchesDb: SearchingDbHelper
) : ViewModel() {
    private val TAG = "LyricsDownloaderPageViewModel"

    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val state: LyricsDownloaderPageState = LyricsDownloaderPageState.Loading,
        val filter: MediaStoreFilterType? = null
    )

    /**
     * Loads all songs from the media store db
     * @return a list of songs
     */
    suspend fun loadMediaStoreTracks(
        context: Context
    ): List<Song> {
        updateState(LyricsDownloaderPageState.Loading)

        val songs = withContext(Dispatchers.IO) {
            MediaStoreReceiver.getAllSongsFromMediaStore(
                applicationContext = context,
            )
        }

        updateState(LyricsDownloaderPageState.Loaded(songs))

        return songs
    }

    /**
     * Loads all songs from the media store db
     * @return a list of songs
     */
    suspend fun loadMediaStoreWithFilter(
        context: Context,
        filter: String,
        filterType: MediaStoreFilterType? = pageViewState.value.filter
    ) {
        updateState(LyricsDownloaderPageState.Loading)

        val songs = withContext(Dispatchers.IO) {
            MediaStoreReceiver.getAllSongsFromMediaStore(
                applicationContext = context, searchTerm = filter, filterType = filterType
            )
        }

        updateState(LyricsDownloaderPageState.Loaded(songs))
    }

    suspend fun insertSearch(
        search: String,
        filter: MediaStoreFilterType? = pageViewState.value.filter,
        spotifySearch: Boolean = false
    ) {
        searchesDb.insertSearch(search, filter, spotifySearch)
    }

    suspend fun getAllSearches(): List<SearchEntity> {
        return searchesDb.getAllSearches()
    }

    fun allSearchesFlow() = searchesDb.getAllSearchesWithFlow()

    suspend fun getSearchById(searchId: Int): SearchEntity? {
        return searchesDb.getSearchById(searchId)
    }

    suspend fun deleteSearchById(searchId: Int) {
        Log.d(TAG, "deleteSearchById: $searchId")
        searchesDb.deleteSearch(searchId)
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

    fun updateFilter(filter: MediaStoreFilterType?) {
        mutablePageViewState.update {
            it.copy(filter = filter)
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
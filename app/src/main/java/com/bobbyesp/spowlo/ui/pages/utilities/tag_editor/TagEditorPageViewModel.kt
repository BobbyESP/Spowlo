package com.bobbyesp.spowlo.ui.pages.utilities.tag_editor

import android.content.Context
import androidx.lifecycle.ViewModel
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.MediaStoreFilterType
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.MediaStoreReceiver
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.main.MediaStorePageState
import com.bobbyesp.spowlo.utils.databases.SearchingDbHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TagEditorPageViewModel @Inject constructor(
    private val searchesDb: SearchingDbHelper
): ViewModel() {
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val state: MediaStorePageState = MediaStorePageState.Loading,
        val filter: MediaStoreFilterType? = null
    )

    /**
     * Loads all songs from the media store db
     * @return a list of songs
     */
    suspend fun loadMediaStoreTracks(
        context: Context
    ): List<Song> {
        updateState(MediaStorePageState.Loading)

        val songs = withContext(Dispatchers.IO) {
            MediaStoreReceiver.getAllSongsFromMediaStore(
                applicationContext = context,
            )
        }

        updateState(MediaStorePageState.Loaded(songs))

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
        updateState(MediaStorePageState.Loading)

        val songs = withContext(Dispatchers.IO) {
            MediaStoreReceiver.getAllSongsFromMediaStore(
                applicationContext = context, searchTerm = filter, filterType = filterType
            )
        }

        updateState(MediaStorePageState.Loaded(songs))
    }

    suspend fun insertSearch(
        search: String,
        filter: MediaStoreFilterType? = pageViewState.value.filter,
        spotifySearch: Boolean = false
    ) {
        searchesDb.insertSearch(search, filter, spotifySearch)
    }

    fun allSearchesFlow() = searchesDb.getAllSearchesWithFlow()

    suspend fun deleteSearchById(searchId: Int) {
        searchesDb.deleteSearch(searchId)
    }

    /**
     * Updates the state of the page
     * @param state the new state
     */
    private fun updateState(state: MediaStorePageState) {
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
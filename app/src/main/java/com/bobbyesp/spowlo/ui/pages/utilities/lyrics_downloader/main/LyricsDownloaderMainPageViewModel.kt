package com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.MediaStoreFilterType
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.MediaStoreReceiver
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import com.bobbyesp.spowlo.utils.databases.SearchingDbHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MediaStorePageViewModel @Inject constructor(
    private val searchesDb: SearchingDbHelper,
    @ApplicationContext applicationContext: Context
) : ViewModel() {
    private val TAG = "LyricsDownloaderPageViewModel"

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadMediaStoreTracks(applicationContext)
        }
    }

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

    fun isLoadedAndSongsAreEmpty() = pageViewState.value.state is MediaStorePageState.Loaded && (pageViewState.value.state as MediaStorePageState.Loaded).mediaStoreSongs.isEmpty()

    suspend fun insertSearch(
        search: String,
        filter: MediaStoreFilterType? = pageViewState.value.filter,
        spotifySearch: Boolean = false
    ) {
        searchesDb.insertSearch(search, filter, spotifySearch)
    }

    fun allSearchesFlow() = searchesDb.getAllSearchesWithFlow()

    suspend fun deleteSearchById(searchId: Int) {
        Log.d(TAG, "deleteSearchById: $searchId")
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

/**
 * The state of the page
 */
sealed class MediaStorePageState {
    object Loading : MediaStorePageState()
    class Loaded(val mediaStoreSongs: List<Song>) : MediaStorePageState()
    object Error : MediaStorePageState()
}
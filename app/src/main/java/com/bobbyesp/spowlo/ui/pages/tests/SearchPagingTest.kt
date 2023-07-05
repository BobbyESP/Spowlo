package com.bobbyesp.spowlo.ui.pages.tests

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.utils.Market
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.toSong
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.TrackPagingSource
import com.bobbyesp.spowlo.ui.components.cards.horizontal.HorizontalSongCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Composable
fun SearchPagingTestPage(
    viewModel: SearchPagingTestViewModel = hiltViewModel<SearchPagingTestViewModel>()
) {
    val tracks = viewModel.getTrackPagingData("Imagine Dragons", Market.ES).collectAsLazyPagingItems()

    LazyColumn {
        this.items(
            count = tracks.itemCount,
            key = tracks.itemKey(),
            contentType = tracks.itemContentType()
        ) { index ->
            val item = tracks[index]

            HorizontalSongCard(song = item!!.toSong())
        }
        tracks.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item {
                        // Render a loading indicator while refreshing
                        CircularProgressIndicator()
                    }
                }

                loadState.append is LoadState.Loading -> {
                    item {
                        // Render a loading indicator at the end while loading more items
                        CircularProgressIndicator()
                    }
                }

                loadState.refresh is LoadState.Error -> {
                    val errorMessage = (loadState.refresh as LoadState.Error).error.message
                    item {
                        // Render an error message if refreshing encounters an error
                        if (errorMessage != null) {
                            Text(errorMessage)
                        }
                    }
                }

                loadState.append is LoadState.Error -> {
                    val errorMessage = (loadState.append as LoadState.Error).error.message
                    item {
                        // Render an error message if loading more items encounters an error
                        if (errorMessage != null) {
                            Text(errorMessage)
                        }
                    }
                }
            }
        }
    }
}

@HiltViewModel
class SearchPagingTestViewModel @Inject constructor(
) : ViewModel() {
    fun getTrackPagingData(query: String, market: Market?): Flow<PagingData<Track>> {

        return Pager(
            config = PagingConfig(pageSize = 50, enablePlaceholders = false),
            pagingSourceFactory = { TrackPagingSource(null, query, market) }
        ).flow.cachedIn(viewModelScope)
    }
}
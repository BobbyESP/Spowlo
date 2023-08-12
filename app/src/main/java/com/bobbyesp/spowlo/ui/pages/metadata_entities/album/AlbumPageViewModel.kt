package com.bobbyesp.spowlo.ui.pages.metadata_entities.album

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.Album
import com.adamratzman.spotify.models.SimpleTrack
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.SpotifyApiRequests
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.sp_app.AlbumTracksPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AlbumPageViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val state: AlbumPageState = AlbumPageState.Loading,
        val albumTracksPaginated: Flow<PagingData<SimpleTrack>> = emptyFlow(),
        val trackForSheet : SimpleTrack? = null,
        val dominantColor: Color? = null,
    )

    suspend fun loadAlbum(id: String) {
        try {
            val spotifyAppApi: SpotifyAppApi = SpotifyApiRequests.provideSpotifyApi()
            viewModelScope.launch(Dispatchers.IO) {
                if (pageViewState.value.state != AlbumPageState.Loading) updateState(AlbumPageState.Loading)
                val albumDeferred = withContext(Dispatchers.IO) {
                    async { spotifyAppApi.albums.getAlbum(id) }
                }
                val album = albumDeferred.await()
                    ?: throw Exception(context.getString(R.string.album_not_found))

                updateState(AlbumPageState.Success(album))

                getAlbumTracksPaginated(id)
            }
        } catch (e: Exception) {
            updateState(AlbumPageState.Error(e.message ?: "Unknown error"))
        }
    }

    private fun getAlbumTracksPaginated(albumId: String) {
        val albumTracksPager = Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 40,
            ),
            pagingSourceFactory = { AlbumTracksPagingSource(albumId = albumId) }
        ).flow.cachedIn(viewModelScope)

        mutablePageViewState.update {
            it.copy(
                albumTracksPaginated = albumTracksPager
            )
        }
    }

    private fun updateState(state: AlbumPageState) {
        mutablePageViewState.update {
            it.copy(
                state = state
            )
        }
    }

    fun selectTrackForSheet(track: SimpleTrack) {
        mutablePageViewState.update {
            it.copy(
                trackForSheet = track
            )
        }
    }

    companion object {
        sealed class AlbumPageState {
            data object Loading : AlbumPageState()
            data class Error(val e: String) : AlbumPageState()
            data class Success(val album: Album) : AlbumPageState()
        }
    }
}
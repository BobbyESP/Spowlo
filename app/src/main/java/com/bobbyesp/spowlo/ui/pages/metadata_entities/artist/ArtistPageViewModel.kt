package com.bobbyesp.spowlo.ui.pages.metadata_entities.artist

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.SpotifyApiRequests
import com.bobbyesp.spowlo.utils.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ArtistPageViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val state: ArtistPageState = ArtistPageState.Loading,
        val artistTopTracks: Resource<List<Track>> = Resource.Loading(),
        val trackForSheet: Track? = null,
        val dominantColor: Color? = null,
    )

    suspend fun loadArtist(id: String) {
        try {
            val spotifyAppApi: SpotifyAppApi = SpotifyApiRequests.provideSpotifyApi()
            if (pageViewState.value.state != ArtistPageState.Loading) updateState(ArtistPageState.Loading)
            viewModelScope.launch(Dispatchers.IO) {
                val artistDeferred = withContext(Dispatchers.IO) {
                    async { spotifyAppApi.artists.getArtist(id) }
                }
                val artist = artistDeferred.await()
                    ?: throw Exception(context.getString(R.string.artist_not_found))

                updateState(ArtistPageState.Success(artist))

                withContext(Dispatchers.IO) {
                    loadArtistSecondaryData(id, spotifyAppApi)
                }
            }
        } catch (e: Exception) {
            updateState(ArtistPageState.Error(e.message ?: "Unknown error"))
        }
    }

    private suspend fun loadArtistSecondaryData(id: String, spotifyAppApi: SpotifyAppApi) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                getArtistTopTracks(id, spotifyAppApi)
            }
        } catch (e: Exception) {
            Log.e("ArtistPageViewModel", "Error loading artist secondary data like Top Tracks", e)
        }
    }

    private suspend fun getArtistTopTracks(id: String, spotifyAppApi: SpotifyAppApi) {
        try {
            val artistTopTracksDeferred = withContext(Dispatchers.IO) {
                async { spotifyAppApi.artists.getArtistTopTracks(id) }
            }
            val artistTopTracks = artistTopTracksDeferred.await()

            mutablePageViewState.update { it.copy(artistTopTracks = Resource.Success(artistTopTracks)) }
        } catch (e: Exception) {
            mutablePageViewState.update {
                it.copy(
                    artistTopTracks = Resource.Error(
                        e.message ?: "Unknown error"
                    )
                )
            }
        }
    }

    fun selectTrackForSheet(track: Track) {
        mutablePageViewState.update { it.copy(trackForSheet = track) }
    }

    private fun updateState(state: ArtistPageState) {
        mutablePageViewState.update { it.copy(state = state) }
    }

    companion object {
        sealed class ArtistPageState {
            data object Loading : ArtistPageState()
            data class Success(val artist: Artist) : ArtistPageState()
            data class Error(val message: String) : ArtistPageState()
        }
    }
}
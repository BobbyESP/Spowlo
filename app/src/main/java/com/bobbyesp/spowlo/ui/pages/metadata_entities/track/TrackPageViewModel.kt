package com.bobbyesp.spowlo.ui.pages.metadata_entities.track

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.AudioAnalysis
import com.adamratzman.spotify.models.AudioFeatures
import com.adamratzman.spotify.models.SimpleArtist
import com.adamratzman.spotify.models.Track
import com.bobbyesp.color.utils.PaletteGenerator
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
class TrackPageViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val state: TrackPageState = TrackPageState.Loading,
        val artists: List<Artist> = emptyList(),
        val artistsImages: List<String> = emptyList(),
        val audioAnalysisData: Resource<AudioAnalysis> = Resource.Loading(),
        val audioFeaturesData : Resource<AudioFeatures> = Resource.Loading(),
        val dominantColor: Color? = null,
    )

    suspend fun loadTrack(id: String) {
        try {
            val spotifyAppApi: SpotifyAppApi = SpotifyApiRequests.provideSpotifyApi()
            viewModelScope.launch(Dispatchers.IO) {
                if (pageViewState.value.state != TrackPageState.Loading) updateState(TrackPageState.Loading)
                val trackDeferred = withContext(Dispatchers.IO) {
                    async { spotifyAppApi.tracks.getTrack(id) }
                }
                val track = trackDeferred.await()
                    ?: throw Exception(context.getString(R.string.track_not_found))

                updateState(TrackPageState.Success(track))

                loadSecondaryData(track, spotifyAppApi)
            }
        } catch (e: Exception) {
            updateState(
                TrackPageState.Error(
                    e.message ?: context.getString(R.string.unknown_error)
                )
            )
        }
    }

    private suspend fun loadSecondaryData(track: Track, spotifyAppApi: SpotifyAppApi) {

        val trackId = track.id

        try {
            viewModelScope.launch(Dispatchers.IO) {
                val artistsImages = getArtistsImages(track.artists)

                val artists = getArtists(track.artists)

                mutablePageViewState.update {
                    it.copy(
                        artists = artists,
                        artistsImages = artistsImages
                    )
                }

                val artworkUrl = track.album.images.firstOrNull()?.url ?: ""

                getDominantColor(
                    PaletteGenerator.loadBitmapFromUrl(
                        context, artworkUrl
                    )
                )

                loadAudioFeatures(trackId, spotifyAppApi)
                loadAudioAnalysis(trackId, spotifyAppApi)
            }
        } catch (e: Exception) {
            Log.e("TrackPageViewModel", "secondaryDataLoad: ${e.message}")
        }
    }

    private suspend fun loadAudioAnalysis(trackId: String, spotifyAppApi: SpotifyAppApi) {
        val audioAnalysisDeferred = withContext(Dispatchers.IO) {
            async { spotifyAppApi.tracks.getAudioAnalysis(trackId) }
        }
        val audioAnalysis = audioAnalysisDeferred.await()

        mutablePageViewState.update {
            it.copy(
                audioAnalysisData = Resource.Success(audioAnalysis)
            )
        }
    }

    private suspend fun loadAudioFeatures(trackId: String, spotifyAppApi: SpotifyAppApi) {
        val audioFeaturesDeferred = withContext(Dispatchers.IO) {
            async { spotifyAppApi.tracks.getAudioFeatures(trackId) }
        }
        val audioFeatures = audioFeaturesDeferred.await()

        mutablePageViewState.update {
            it.copy(
                audioFeaturesData = Resource.Success(audioFeatures)
            )
        }
    }

    private suspend fun getArtistsImages(artists: List<SimpleArtist>): List<String> {
        val images = mutableListOf<String>()
        artists.forEach { artist ->
            val newArtistDeferred = withContext(Dispatchers.IO) {
                async { artist.toFullArtist() }
            }
            val newArtist = newArtistDeferred.await()
            val image = newArtist?.images?.firstOrNull()?.url

            if (image != null) images.add(image)
        }
        return images
    }

    private fun getDominantColor(bitmap: Bitmap?) {
        if (bitmap == null) return
        val color = PaletteGenerator.getDominantColor(bitmap)
        mutablePageViewState.update {
            it.copy(
                dominantColor = color
            )
        }

    }

    private suspend fun getArtists(simpleArtists: List<SimpleArtist>): List<Artist> {
        return simpleArtists.mapNotNull { artist ->
            withContext(Dispatchers.IO) {
                artist.toFullArtist()
            }
        }
    }


    private fun updateState(state: TrackPageState) {
        mutablePageViewState.update {
            it.copy(
                state = state
            )
        }
    }

    companion object {
        sealed class TrackPageState {
            data object Loading : TrackPageState()
            data class Error(val e: String) : TrackPageState()
            data class Success(
                val track: Track
            ) : TrackPageState()
        }
    }
}
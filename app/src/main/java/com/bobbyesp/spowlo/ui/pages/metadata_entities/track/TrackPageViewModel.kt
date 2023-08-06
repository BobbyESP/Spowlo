package com.bobbyesp.spowlo.ui.pages.metadata_entities.track

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.SimpleArtist
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.SpotifyApiRequests
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
        val dominantColor: Color? = null,
    )

    suspend fun loadTrack(id: String) {
        val spotifyAppApi: SpotifyAppApi = SpotifyApiRequests.provideSpotifyApi()
        try {
            if (pageViewState.value.state != TrackPageState.Loading) updateState(TrackPageState.Loading)
            val trackDeferred = withContext(Dispatchers.IO) {
                async { spotifyAppApi.tracks.getTrack(id) }
            }
            val track = trackDeferred.await()
                ?: throw Exception(context.getString(R.string.track_not_found))

            val artistsImages = getArtistsImages(track.artists)

            val artists = getArtists(track.artists)

            updateState(TrackPageState.Success(track, artistsImages, artists))
        } catch (e: Exception) {
            updateState(
                TrackPageState.Error(
                    e.message ?: context.getString(R.string.unknown_error)
                )
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

    fun getDominantColor(bitmap: Bitmap?) {
        if(bitmap == null) return
        Palette.from(bitmap).generate().let { palette ->
            val color = palette.vibrantSwatch?.rgb?.let { Color(it) }
            Log.i("TrackPageViewModel", "getDominantColor: $color")
            mutablePageViewState.update {
                it.copy(
                    dominantColor = color
                )
            }
        }
    }

    suspend fun getArtists(simpleArtists: List<SimpleArtist>): List<Artist> {
        val artists = mutableListOf<Artist>()

        simpleArtists.forEach { artist ->
            val newArtistDeferred = withContext(Dispatchers.IO) {
                async { artist.toFullArtist() }
            }

            val newArtist = newArtistDeferred.await()
            if (newArtist != null) artists.add(newArtist)
        }
        return artists
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
            data class Success(val track: Track, val artistsImages: List<String>, val artists: List<Artist>) : TrackPageState()
        }
    }
}
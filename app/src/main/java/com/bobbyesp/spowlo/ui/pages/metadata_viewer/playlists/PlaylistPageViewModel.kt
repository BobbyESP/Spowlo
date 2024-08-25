package com.bobbyesp.spowlo.ui.pages.metadata_viewer.playlists

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bobbyesp.spowlo.Downloader
import com.bobbyesp.spowlo.features.spotify_api.data.remote.SpotifyApiRequests
import com.bobbyesp.spowlo.features.spotify_api.model.SpotifyDataType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaylistPageViewModel @Inject constructor() : ViewModel() {

    private val mutableViewStateFlow = MutableStateFlow(ViewState())
    val viewStateFlow = mutableViewStateFlow.asStateFlow()

    data class ViewState(
        val id: String = "",
        val state: PlaylistDataState = PlaylistDataState.Loading,
    )

    suspend fun loadData(id: String, type: SpotifyDataType = SpotifyDataType.TRACK) {
        mutableViewStateFlow.update {
            it.copy(
                id = id,
                state = PlaylistDataState.Loading
            )
        }
        when (type) {
            SpotifyDataType.TRACK -> {
                try {
                    val trackDeferred = withContext(Dispatchers.IO) {
                        async {
                            Log.d("SpotifyApiRequests", "provideGetTrackById($id)")
                            SpotifyApiRequests.provideGetTrackById(id)
                        }
                    }
                    val track = trackDeferred.await()

                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Loaded(
                                track!!
                            )
                        )
                    }
                } catch (e: Exception) {
                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Error(Exception("Error while loading data"))
                        )
                    }
                }
            }

            SpotifyDataType.ALBUM -> {
                try {
                    val albumDeferred = withContext(Dispatchers.IO) {
                        async {
                            Log.d("SpotifyApiRequests", "providesGetAlbumById($id)")
                            SpotifyApiRequests.providesGetAlbumById(id)
                        }
                    }
                    val album = albumDeferred.await()

                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Loaded(
                                album!!
                            )
                        )
                    }
                } catch (e: Exception) {
                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Error(Exception("Error while loading data"))
                        )
                    }
                }

            }

            SpotifyDataType.PLAYLIST -> {
                try {
                    val playlistDeferred = withContext(Dispatchers.IO) {
                        async {
                            Log.d("SpotifyApiRequests", "provideGetPlaylistById($id)")
                            SpotifyApiRequests.provideGetPlaylistById(id)
                        }
                    }
                    val playlist = playlistDeferred.await()

                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Loaded(
                                playlist!!
                            )
                        )
                    }
                } catch (e: Exception) {
                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Error(Exception("Error while loading data"))
                        )
                    }
                }
            }

            SpotifyDataType.ARTIST -> {
                try {
                    val artistDeferred = withContext(Dispatchers.IO) {
                        async {
                            Log.d("SpotifyApiRequests", "provideGetArtistById($id)")
                            SpotifyApiRequests.provideGetArtistById(id)
                        }
                    }
                    val artist = artistDeferred.await()

                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Loaded(
                                artist!!
                            )
                        )
                    }
                } catch (e: Exception) {
                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Error(Exception("Error while loading data"))
                        )
                    }
                }
            }
        }
    }

    fun downloadTrack(url: String, name: String) {
        Downloader.executeParallelDownloadWithUrl(url, name)
    }
}

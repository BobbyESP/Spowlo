package com.bobbyesp.spowlo.ui.pages.metadata_viewer.playlists

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bobbyesp.spowlo.Downloader
import com.bobbyesp.spowlo.features.spotify_api.data.remote.SpotifyApiRequests
import com.bobbyesp.spowlo.features.spotify_api.model.SpotifyDataType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
                kotlin.runCatching {
                    Log.d("SpotifyApiRequests", "provideGetTrackById($id)")
                    SpotifyApiRequests.provideGetTrackById(id)
                }.onSuccess { data ->
                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Loaded(
                                data!!
                            )
                        )
                    }
                }.onFailure {
                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Error(Exception("Error while loading data"))
                        )
                    }
                }
            }

            SpotifyDataType.ALBUM -> {
                kotlin.runCatching {
                    SpotifyApiRequests.providesGetAlbumById(id)
                }.onSuccess { data ->
                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Loaded(
                                data!!
                            )
                        )
                    }
                }.onFailure {
                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Error(Exception("Error while loading data"))
                        )
                    }
                }

            }

            SpotifyDataType.PLAYLIST -> {
                kotlin.runCatching {
                    Log.d("SpotifyApiRequests", "provideGetPlaylistById($id)")
                    SpotifyApiRequests.provideGetPlaylistById(id)
                }.onSuccess { data ->
                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Loaded(
                                data!!
                            )
                        )
                    }
                }.onFailure {
                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Error(Exception("Error while loading data"))
                        )
                    }
                }

            }

            SpotifyDataType.ARTIST -> {

            }
        }
    }

    fun downloadTrack(url: String, name: String) {
        Downloader.executeParallelDownloadWithUrl(url, name)
    }

}



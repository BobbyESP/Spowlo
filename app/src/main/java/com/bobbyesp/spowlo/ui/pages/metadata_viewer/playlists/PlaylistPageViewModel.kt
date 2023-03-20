package com.bobbyesp.spowlo.ui.pages.metadata_viewer.playlists

import androidx.lifecycle.ViewModel
import com.adamratzman.spotify.models.ReleaseDate
import com.bobbyesp.spowlo.features.spotify_api.model.SpotifyData
import com.bobbyesp.spowlo.features.spotify_api.model.SpotifyDataType
import com.bobbyesp.spowlo.features.spotify_api.data.remote.SpotifyApiRequests
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.binders.typeOfSpotifyDataType
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
        when (type) {
            SpotifyDataType.TRACK -> {
                kotlin.runCatching {
                    SpotifyApiRequests.getTrackById(id)
                }.onSuccess { data ->
                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Loaded(
                                SpotifyData(
                                    data!!.album.images[0].url,
                                    data.name,
                                    data.artists.map { it.name },
                                    data.album.releaseDate,
                                    type = typeOfSpotifyDataType(data.type),
                                )
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
                    SpotifyApiRequests.getAlbumById(id)
                }.onSuccess { data ->
                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Loaded(
                                SpotifyData(
                                    data!!.images[0].url,
                                    data.name,
                                    data.artists.map { it.name },
                                    data.releaseDate,
                                    type = typeOfSpotifyDataType(data.type),
                                )
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
                    SpotifyApiRequests.getPlaylistById(id)
                }.onSuccess { data ->
                    mutableViewStateFlow.update {
                        it.copy(
                            state = PlaylistDataState.Loaded(
                                SpotifyData(
                                    data!!.images[0].url,
                                    data.name,
                                    listOf(data.owner.displayName ?: data.owner.id),
                                    ReleaseDate(2000, 1, 1),
                                    data.tracks.total,
                                    typeOfSpotifyDataType(data.type),
                                )
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
}



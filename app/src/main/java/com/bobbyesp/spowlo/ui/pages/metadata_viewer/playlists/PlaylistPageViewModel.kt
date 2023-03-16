package com.bobbyesp.spowlo.ui.pages.metadata_viewer.playlists

import androidx.lifecycle.ViewModel
import com.bobbyesp.spowlo.features.spotify_api.SpotifyApiRequests
import com.bobbyesp.spowlo.features.spotify_api.data.dtos.SpotifyData
import com.bobbyesp.spowlo.features.spotify_api.data.dtos.SpotifyDataType
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
        kotlin.runCatching {
            SpotifyApiRequests.searchTrackById(id)
        }.onSuccess { track ->
            mutableViewStateFlow.update {
                it.copy(
                    state = PlaylistDataState.Loaded(
                        SpotifyData(
                            track!!.album.images[0].url,
                            track.name,
                            track.artists.map { it.name },
                            track.album.releaseDate,
                            typeOfSpotifyDataType(track.type),
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
}
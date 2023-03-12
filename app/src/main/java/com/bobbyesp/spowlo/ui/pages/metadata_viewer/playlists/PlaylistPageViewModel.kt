package com.bobbyesp.spowlo.ui.pages.metadata_viewer.playlists

import androidx.lifecycle.ViewModel
import com.bobbyesp.spowlo.features.spotify_api.data.dtos.SpotifyData
import com.bobbyesp.spowlo.features.spotify_api.data.dtos.SpotifyDataType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class PlaylistPageViewModel @Inject constructor() : ViewModel() {

    private val mutableViewStateFlow = MutableStateFlow(ViewState())
    val viewStateFlow = mutableViewStateFlow.asStateFlow()

    data class ViewState(
        val id : String = "",
        val state : PlaylistDataState = PlaylistDataState.Loading,
    )

    suspend fun loadData(){
        mutableViewStateFlow.update {
            it.copy(state = PlaylistDataState.Loaded(SpotifyData("", "Faded", listOf("Alan Walker"), SpotifyDataType.TRACK)))
        }
    }
}
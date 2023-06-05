package com.bobbyesp.appmodules.hub.ui.screens.viewModels

import com.bobbyesp.appmodules.core.SpotifySessionManager
import com.bobbyesp.appmodules.core.api.SpotifyPartnersApi
import com.bobbyesp.appmodules.core.api.interalApi.SpotifyInternalApi
import com.bobbyesp.appmodules.core.objects.player.PlayFromContextData
import com.bobbyesp.appmodules.hub.ui.screens.AbstractHubViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaylistPageViewModel @Inject constructor(
    private val spSessionManager: SpotifySessionManager,
    private val spInternalApi: SpotifyInternalApi,
    private val spPartnersApi: SpotifyPartnersApi,
    //private val spPlayerServiceManager: SpotifyPlayerServiceManager,
    //private val spMetadataRequester: SpotifyMetadataRequester
): AbstractHubViewModel() {
    override fun play(data: PlayFromContextData) {
        TODO("Not yet implemented")
    }
}
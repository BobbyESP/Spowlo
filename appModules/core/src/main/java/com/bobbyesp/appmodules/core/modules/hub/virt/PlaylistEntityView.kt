package com.bobbyesp.appmodules.core.modules.hub.virt

import com.bobbyesp.appmodules.core.objects.ui_components.UiResponse
import com.spotify.metadata.Metadata
import com.spotify.playlist4.Playlist4ApiProto

object PlaylistEntityView {
    class ApiPlaylist(
        val playlist: Playlist4ApiProto.SelectedListContent,
        val playlistTrackMetadata: List<Playlist4ApiProto.Item>,
        val trackMetadata: Map<String, Metadata.Track>,
        val hubResponse: UiResponse
    )
}
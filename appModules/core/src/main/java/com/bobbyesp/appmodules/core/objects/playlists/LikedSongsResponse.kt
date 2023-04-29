package com.bobbyesp.appmodules.core.objects.playlists

import bruhcollective.itaysonlab.jetispot.ui.hub.virt.PlaylistEntityView
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class LikedSongsResponse(
    val href: String? = "",
    val items: List<PlaylistEntityView.ApiPlaylist>,
    val limit: Int? = 0,
    val next: String? = "",
    val offset: Int? = 0,
    val previous: String? = "",
    val total: Int? = 0,
)
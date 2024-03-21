package com.zionhuang.innertube.pages

import com.zionhuang.innertube.ext.album
import com.zionhuang.innertube.ext.artists
import com.zionhuang.innertube.ext.duration
import com.zionhuang.innertube.ext.isExplicit
import com.zionhuang.innertube.ext.thumbnailUrl
import com.zionhuang.innertube.ext.title
import com.zionhuang.innertube.models.MusicResponsiveListItemRenderer
import com.zionhuang.innertube.models.PlaylistItem
import com.zionhuang.innertube.models.SongItem

data class PlaylistPage(
    val playlist: PlaylistItem,
    val songs: List<SongItem>,
    val songsContinuation: String?,
    val continuation: String?,
) {
    companion object {
        fun fromMusicResponsiveListItemRenderer(renderer: MusicResponsiveListItemRenderer): SongItem? {
            return SongItem(
                id = renderer.playlistItemData?.videoId ?: return null,
                title = renderer.title() ?: return null,
                artists = renderer.artists() ?: return null,
                album = renderer.album(),
                duration = renderer.duration(),
                thumbnail = renderer.thumbnailUrl()
                    ?: return null,
                explicit = renderer.isExplicit(),
                endpoint = renderer.overlay?.musicItemThumbnailOverlayRenderer?.content?.musicPlayButtonRenderer?.playNavigationEndpoint?.watchEndpoint
            )
        }
    }
}

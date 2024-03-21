package com.zionhuang.innertube.pages

import com.zionhuang.innertube.ext.album
import com.zionhuang.innertube.ext.artists
import com.zionhuang.innertube.ext.duration
import com.zionhuang.innertube.ext.isExplicit
import com.zionhuang.innertube.ext.thumbnailUrl
import com.zionhuang.innertube.ext.title
import com.zionhuang.innertube.ext.watchEndpoint
import com.zionhuang.innertube.models.MusicResponsiveListItemRenderer
import com.zionhuang.innertube.models.SongItem
import com.zionhuang.innertube.models.YTItem

data class ArtistItemsContinuationPage(
    val items: List<YTItem>,
    val continuation: String?,
) {
    companion object {
        fun fromMusicResponsiveListItemRenderer(renderer: MusicResponsiveListItemRenderer): SongItem? {
            // Extracting id
            val id = renderer.playlistItemData?.videoId ?: return null

            // Extracting title
            val title = renderer.title() ?: return null

            // Extracting artists
            val artists = renderer.artists() ?: return null

            // Extracting album
            val album = renderer.album()

            // Extracting duration
            val duration = renderer.duration() ?: return null

            // Extracting thumbnail
            val thumbnail = renderer.thumbnailUrl()
                ?: return null

            // Checking if the explicit badge is present
            val explicit = renderer.isExplicit()

            // Extracting endpoint
            val endpoint = renderer.watchEndpoint()
            // Constructing and returning SongItem
            return SongItem(
                id = id,
                title = title,
                artists = artists,
                album = album,
                duration = duration,
                thumbnail = thumbnail,
                explicit = explicit,
                endpoint = endpoint
            )
        }
    }
}

package com.zionhuang.innertube.pages

import com.zionhuang.innertube.ext.artists
import com.zionhuang.innertube.ext.duration
import com.zionhuang.innertube.ext.isExplicit
import com.zionhuang.innertube.ext.thumbnailUrl
import com.zionhuang.innertube.ext.title
import com.zionhuang.innertube.models.Album
import com.zionhuang.innertube.models.AlbumItem
import com.zionhuang.innertube.models.MusicResponsiveListItemRenderer
import com.zionhuang.innertube.models.SongItem

data class AlbumPage(
    val album: AlbumItem,
    val songs: List<SongItem>,
) {
    companion object {
        fun fromMusicResponsiveListItemRenderer(renderer: MusicResponsiveListItemRenderer): SongItem? {
            val playlistItemData = renderer.playlistItemData ?: return null
            val id = playlistItemData.videoId

            val title = renderer.title() ?: return null

            val artists = renderer.artists() ?: return null

            val albumColumn = renderer.flexColumns.getOrNull(2)?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.firstOrNull()
            val album = albumColumn?.let {
                Album(
                    name = it.text,
                    id = it.navigationEndpoint?.browseEndpoint?.browseId!!
                )
            } ?: return null

            val duration = renderer.duration() ?: return null

            val thumbnail = renderer.thumbnailUrl() ?: return null

            val explicit = renderer.isExplicit()

            return SongItem(
                id = id,
                title = title,
                artists = artists,
                album = album,
                duration = duration,
                thumbnail = thumbnail,
                explicit = explicit
            )
        }

    }
}
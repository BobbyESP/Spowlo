package com.zionhuang.innertube.pages

import com.zionhuang.innertube.ext.isExplicit
import com.zionhuang.innertube.ext.playEndpoint
import com.zionhuang.innertube.ext.playlistId
import com.zionhuang.innertube.ext.radioEndpoint
import com.zionhuang.innertube.ext.shuffleEndpoint
import com.zionhuang.innertube.ext.thumbnailUrl
import com.zionhuang.innertube.ext.title
import com.zionhuang.innertube.models.Album
import com.zionhuang.innertube.models.AlbumItem
import com.zionhuang.innertube.models.Artist
import com.zionhuang.innertube.models.ArtistItem
import com.zionhuang.innertube.models.MusicResponsiveListItemRenderer
import com.zionhuang.innertube.models.PlaylistItem
import com.zionhuang.innertube.models.SongItem
import com.zionhuang.innertube.models.YTItem
import com.zionhuang.innertube.models.oddElements
import com.zionhuang.innertube.models.splitBySeparator
import com.zionhuang.innertube.utils.parseTime

data class SearchResult(
    val items: List<YTItem>,
    val continuation: String? = null,
)

object SearchPage {
    fun toYTItem(renderer: MusicResponsiveListItemRenderer): YTItem? {
        val secondaryLine = renderer.flexColumns.getOrNull(1)
            ?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.splitBySeparator()
            ?: return null
        return when {
            renderer.isSong -> {
                SongItem(
                    id = renderer.playlistItemData?.videoId ?: return null,
                    title = renderer.title() ?: return null,
                    artists = secondaryLine.firstOrNull()?.oddElements()?.map {
                        Artist(
                            name = it.text,
                            id = it.navigationEndpoint?.browseEndpoint?.browseId
                        )
                    } ?: return null,
                    album = secondaryLine.getOrNull(1)?.firstOrNull()
                        ?.takeIf { it.navigationEndpoint?.browseEndpoint != null }?.let {
                            Album(
                                name = it.text,
                                id = it.navigationEndpoint?.browseEndpoint?.browseId!!
                            )
                        },
                    duration = secondaryLine.lastOrNull()?.firstOrNull()?.text?.parseTime(),
                    thumbnail = renderer.thumbnailUrl()
                        ?: return null,
                    explicit = renderer.isExplicit()
                )
            }

            renderer.isArtist -> {
                ArtistItem(
                    id = renderer.navigationEndpoint?.browseEndpoint?.browseId ?: return null,
                    title = renderer.title()
                        ?: return null,
                    thumbnail = renderer.thumbnailUrl()
                        ?: return null,
                    shuffleEndpoint = renderer.shuffleEndpoint()
                        ?: return null,
                    radioEndpoint = renderer.radioEndpoint()
                        ?: return null
                )
            }

            renderer.isAlbum -> {
                AlbumItem(
                    browseId = renderer.navigationEndpoint?.browseEndpoint?.browseId ?: return null,
                    playlistId = renderer.playlistId()
                        ?: return null,
                    title = renderer.title() ?: return null,
                    artists = secondaryLine.getOrNull(1)?.oddElements()?.map {
                        Artist(
                            name = it.text,
                            id = it.navigationEndpoint?.browseEndpoint?.browseId
                        )
                    } ?: return null,
                    year = secondaryLine.getOrNull(2)?.firstOrNull()?.text?.toIntOrNull(),
                    thumbnail = renderer.thumbnailUrl()
                        ?: return null,
                    explicit = renderer.isExplicit()
                )
            }

            renderer.isPlaylist -> {
                PlaylistItem(
                    id = renderer.navigationEndpoint?.browseEndpoint?.browseId?.removePrefix("VL")
                        ?: return null,
                    title = renderer.title() ?: return null,
                    author = secondaryLine.firstOrNull()?.firstOrNull()?.let {
                        Artist(
                            name = it.text,
                            id = it.navigationEndpoint?.browseEndpoint?.browseId
                        )
                    } ?: return null,
                    songCountText = renderer.flexColumns.getOrNull(1)
                        ?.musicResponsiveListItemFlexColumnRenderer?.text?.runs
                        ?.lastOrNull()?.text ?: return null,
                    thumbnail = renderer.thumbnailUrl()
                        ?: return null,
                    playEndpoint = renderer.playEndpoint()
                        ?: return null,
                    shuffleEndpoint = renderer.shuffleEndpoint()
                        ?: return null,
                    radioEndpoint = renderer.radioEndpoint()
                        ?: return null
                )
            }

            else -> null
        }
    }
}

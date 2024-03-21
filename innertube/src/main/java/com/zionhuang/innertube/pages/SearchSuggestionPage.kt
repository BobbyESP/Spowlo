package com.zionhuang.innertube.pages

import com.zionhuang.innertube.ext.album
import com.zionhuang.innertube.ext.artists
import com.zionhuang.innertube.ext.isExplicit
import com.zionhuang.innertube.ext.radioEndpoint
import com.zionhuang.innertube.ext.shuffleEndpoint
import com.zionhuang.innertube.ext.thumbnailUrl
import com.zionhuang.innertube.ext.title
import com.zionhuang.innertube.models.AlbumItem
import com.zionhuang.innertube.models.Artist
import com.zionhuang.innertube.models.ArtistItem
import com.zionhuang.innertube.models.MusicResponsiveListItemRenderer
import com.zionhuang.innertube.models.SongItem
import com.zionhuang.innertube.models.YTItem
import com.zionhuang.innertube.models.oddElements
import com.zionhuang.innertube.models.splitBySeparator

object SearchSuggestionPage {
    fun fromMusicResponsiveListItemRenderer(renderer: MusicResponsiveListItemRenderer): YTItem? {
        return when {
            renderer.isSong -> {
                SongItem(
                    id = renderer.playlistItemData?.videoId ?: return null,
                    title = renderer.title() ?: return null,
                    artists = renderer.artists() ?: return null,
                    album = renderer.album(),
                    duration = null,
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
                    shuffleEndpoint = renderer.shuffleEndpoint(),
                    radioEndpoint = renderer.radioEndpoint()
                )
            }

            renderer.isAlbum -> {
                val secondaryLine = renderer.flexColumns.getOrNull(1)
                    ?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.splitBySeparator()
                    ?: return null
                AlbumItem(
                    browseId = renderer.navigationEndpoint?.browseEndpoint?.browseId ?: return null,
                    playlistId = renderer.menu?.menuRenderer?.items?.find {
                        it.menuNavigationItemRenderer?.icon?.iconType == "MUSIC_SHUFFLE"
                    }?.menuNavigationItemRenderer?.navigationEndpoint?.watchPlaylistEndpoint?.playlistId
                        ?: return null,
                    title = renderer.flexColumns.firstOrNull()
                        ?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.firstOrNull()
                        ?.text ?: return null,
                    artists = secondaryLine.getOrNull(1)?.oddElements()?.map {
                        Artist(
                            name = it.text,
                            id = it.navigationEndpoint?.browseEndpoint?.browseId
                        )
                    } ?: return null,
                    year = secondaryLine.lastOrNull()?.firstOrNull()?.text?.toIntOrNull(),
                    thumbnail = renderer.thumbnailUrl()
                        ?: return null,
                    explicit = renderer.isExplicit()
                )
            }

            else -> null
        }
    }
}

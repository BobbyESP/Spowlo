package com.zionhuang.innertube.ext

import com.zionhuang.innertube.models.Album
import com.zionhuang.innertube.models.Artist
import com.zionhuang.innertube.models.MusicResponsiveListItemRenderer
import com.zionhuang.innertube.models.oddElements
import com.zionhuang.innertube.utils.parseTime

fun MusicResponsiveListItemRenderer.title() = flexColumns.firstOrNull()
    ?.musicResponsiveListItemFlexColumnRenderer?.text
    ?.runs?.firstOrNull()?.text

fun MusicResponsiveListItemRenderer.artists() = flexColumns.getOrNull(1)?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.oddElements()
    ?.map {
        Artist(
            name = it.text,
            id = it.navigationEndpoint?.browseEndpoint?.browseId
        )
    }

fun MusicResponsiveListItemRenderer.album() = flexColumns.getOrNull(2)?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.firstOrNull()
    ?.let {
        Album(
            name = it.text,
            id = it.navigationEndpoint?.browseEndpoint?.browseId ?: return null
        )
    }

fun MusicResponsiveListItemRenderer.duration() = fixedColumns?.firstOrNull()
    ?.musicResponsiveListItemFlexColumnRenderer?.text
    ?.runs?.firstOrNull()
    ?.text?.parseTime()

fun MusicResponsiveListItemRenderer.thumbnailUrl() = thumbnail?.musicThumbnailRenderer?.getThumbnailUrl()

fun MusicResponsiveListItemRenderer.isExplicit() = badges?.find {
    it.musicInlineBadgeRenderer.icon.iconType == "MUSIC_EXPLICIT_BADGE"
} != null

fun MusicResponsiveListItemRenderer.playlistId() = overlay?.musicItemThumbnailOverlayRenderer?.content?.musicPlayButtonRenderer?.playNavigationEndpoint?.watchPlaylistEndpoint?.playlistId

/**
 * @return The watch endpoint of the music item to play
 */
fun MusicResponsiveListItemRenderer.watchEndpoint() = overlay?.musicItemThumbnailOverlayRenderer?.content?.musicPlayButtonRenderer?.playNavigationEndpoint?.watchEndpoint

fun MusicResponsiveListItemRenderer.playEndpoint() = overlay?.musicItemThumbnailOverlayRenderer?.content
    ?.musicPlayButtonRenderer?.playNavigationEndpoint?.watchPlaylistEndpoint
fun MusicResponsiveListItemRenderer.shuffleEndpoint() = menu?.menuRenderer?.items
    ?.find { it.menuNavigationItemRenderer?.icon?.iconType == "MUSIC_SHUFFLE" }
    ?.menuNavigationItemRenderer?.navigationEndpoint?.watchPlaylistEndpoint

fun MusicResponsiveListItemRenderer.radioEndpoint() = menu?.menuRenderer?.items
    ?.find { it.menuNavigationItemRenderer?.icon?.iconType == "MIX" }
    ?.menuNavigationItemRenderer?.navigationEndpoint?.watchPlaylistEndpoint

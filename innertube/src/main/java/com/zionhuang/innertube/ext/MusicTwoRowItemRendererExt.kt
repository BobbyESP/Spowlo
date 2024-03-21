package com.zionhuang.innertube.ext

import com.zionhuang.innertube.models.Artist
import com.zionhuang.innertube.models.MusicTwoRowItemRenderer
import com.zionhuang.innertube.models.oddElements
import com.zionhuang.innertube.models.splitBySeparator

fun MusicTwoRowItemRenderer.browseId() = navigationEndpoint.browseEndpoint?.browseId

fun MusicTwoRowItemRenderer.playlistId() =
    thumbnailOverlay?.musicItemThumbnailOverlayRenderer?.content?.musicPlayButtonRenderer?.playNavigationEndpoint?.watchPlaylistEndpoint?.playlistId

fun MusicTwoRowItemRenderer.title() = title.runs?.firstOrNull()?.text

fun MusicTwoRowItemRenderer.artists() =
    subtitle?.runs?.splitBySeparator()?.firstOrNull()?.oddElements()?.map {
            Artist(
                name = it.text, id = it.navigationEndpoint?.browseEndpoint?.browseId
            )
        }

fun MusicTwoRowItemRenderer.artists(index: Int) =
    subtitle?.runs?.splitBySeparator()?.getOrNull(index)?.oddElements()?.map {
        Artist(
            name = it.text, id = it.navigationEndpoint?.browseEndpoint?.browseId
        )
    }

fun MusicTwoRowItemRenderer.author() = subtitle?.runs?.getOrNull(2)?.let {
    Artist(
        name = it.text, id = it.navigationEndpoint?.browseEndpoint?.browseId
    )
}

fun MusicTwoRowItemRenderer.isExplicit() = subtitleBadges?.find {
    it.musicInlineBadgeRenderer.icon.iconType == "MUSIC_EXPLICIT_BADGE"
} != null

fun MusicTwoRowItemRenderer.thumbnailUrl() =
    thumbnailRenderer.musicThumbnailRenderer?.getThumbnailUrl()

fun MusicTwoRowItemRenderer.year() = subtitle?.runs?.lastOrNull()?.text?.toIntOrNull()

fun MusicTwoRowItemRenderer.videoId() = navigationEndpoint.watchEndpoint?.videoId

fun MusicTwoRowItemRenderer.duration() = subtitle?.runs?.getOrNull(4)?.text

fun MusicTwoRowItemRenderer.playEndpoint() =
    thumbnailOverlay?.musicItemThumbnailOverlayRenderer?.content?.musicPlayButtonRenderer?.playNavigationEndpoint?.watchPlaylistEndpoint

fun MusicTwoRowItemRenderer.shuffleEndpoint() = menu?.menuRenderer?.items?.find {
    it.menuNavigationItemRenderer?.icon?.iconType == "MUSIC_SHUFFLE"
}?.menuNavigationItemRenderer?.navigationEndpoint?.watchPlaylistEndpoint

fun MusicTwoRowItemRenderer.radioEndpoint() = menu?.menuRenderer?.items?.find {
    it.menuNavigationItemRenderer?.icon?.iconType == "MIX"
}?.menuNavigationItemRenderer?.navigationEndpoint?.watchPlaylistEndpoint
package com.zionhuang.innertube.pages

import com.zionhuang.innertube.ext.album
import com.zionhuang.innertube.ext.artists
import com.zionhuang.innertube.ext.author
import com.zionhuang.innertube.ext.browseId
import com.zionhuang.innertube.ext.isExplicit
import com.zionhuang.innertube.ext.playEndpoint
import com.zionhuang.innertube.ext.playlistId
import com.zionhuang.innertube.ext.radioEndpoint
import com.zionhuang.innertube.ext.shuffleEndpoint
import com.zionhuang.innertube.ext.thumbnailUrl
import com.zionhuang.innertube.ext.title
import com.zionhuang.innertube.ext.year
import com.zionhuang.innertube.models.AlbumItem
import com.zionhuang.innertube.models.ArtistItem
import com.zionhuang.innertube.models.MusicResponsiveListItemRenderer
import com.zionhuang.innertube.models.MusicTwoRowItemRenderer
import com.zionhuang.innertube.models.PlaylistItem
import com.zionhuang.innertube.models.SongItem
import com.zionhuang.innertube.models.YTItem

data class RelatedPage(
    val songs: List<SongItem>,
    val albums: List<AlbumItem>,
    val artists: List<ArtistItem>,
    val playlists: List<PlaylistItem>,
) {
    companion object {
        fun fromMusicResponsiveListItemRenderer(renderer: MusicResponsiveListItemRenderer): SongItem? {
            return SongItem(
                id = renderer.playlistItemData?.videoId ?: return null,
                title = renderer.title()?: return null,
                artists = renderer.artists() ?: return null,
                album = renderer.album(),
                duration = null,
                thumbnail = renderer.thumbnailUrl()
                    ?: return null,
                explicit = renderer.isExplicit()
            )
        }

        fun fromMusicTwoRowItemRenderer(renderer: MusicTwoRowItemRenderer): YTItem? {
            return when {
                renderer.isAlbum -> AlbumItem(
                    browseId = renderer.browseId() ?: return null,
                    playlistId = renderer.playlistId() ?: return null,
                    title = renderer.title() ?: return null,
                    artists = null,
                    year = renderer.year(),
                    thumbnail = renderer.thumbnailUrl()
                        ?: return null,
                    explicit = renderer.isExplicit()
                )

                renderer.isPlaylist -> PlaylistItem(
                    id = renderer.browseId()?.removePrefix("VL")
                        ?: return null,
                    title = renderer.title() ?: return null,
                    author = renderer.author(),
                    songCountText = renderer.subtitle?.runs?.getOrNull(4)?.text,
                    thumbnail = renderer.thumbnailUrl()
                        ?: return null,
                    playEndpoint = renderer.playEndpoint() ?: return null,
                    shuffleEndpoint = renderer.shuffleEndpoint()
                        ?: return null,
                    radioEndpoint = renderer.radioEndpoint()
                        ?: return null
                )

                renderer.isArtist -> {
                    ArtistItem(
                        id = renderer.browseId() ?: return null,
                        title = renderer.title() ?: return null,
                        thumbnail = renderer.thumbnailUrl()
                            ?: return null,
                        shuffleEndpoint = renderer.shuffleEndpoint()
                            ?: return null,
                        radioEndpoint = renderer.radioEndpoint()
                            ?: return null,
                    )
                }

                else -> null
            }
        }
    }
}

package com.zionhuang.innertube.pages

import com.zionhuang.innertube.ext.artists
import com.zionhuang.innertube.ext.browseId
import com.zionhuang.innertube.ext.isExplicit
import com.zionhuang.innertube.ext.playlistId
import com.zionhuang.innertube.ext.thumbnailUrl
import com.zionhuang.innertube.ext.title
import com.zionhuang.innertube.ext.year
import com.zionhuang.innertube.models.AlbumItem
import com.zionhuang.innertube.models.MusicTwoRowItemRenderer

object NewReleaseAlbumPage {
    fun fromMusicTwoRowItemRenderer(renderer: MusicTwoRowItemRenderer): AlbumItem? {
        return AlbumItem(
            browseId = renderer.browseId() ?: return null,
            playlistId = renderer.playlistId() ?: return null,
            title = renderer.title() ?: return null,
            artists = renderer.artists(1) ?: return null,
            year = renderer.year(),
            thumbnail = renderer.thumbnailUrl()
                ?: return null,
            explicit = renderer.isExplicit()
        )
    }
}

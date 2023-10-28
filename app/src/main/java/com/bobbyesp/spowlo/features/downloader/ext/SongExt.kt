package com.bobbyesp.spowlo.features.downloader.ext

import com.bobbyesp.library.dto.Song
import com.bobbyesp.spowlo.features.downloader.domain.DownloadTaskItem

fun Song.toDownloadTaskItem(preferencesHash: Int) = DownloadTaskItem(
    info = this,
    spotifyUrl = this.url,
    name = this.name,
    artist = this.artist,
    duration = this.duration,
    isExplicit = this.explicit,
    hasLyrics = this.lyrics.isNullOrEmpty(),
    progress = 0f,
    progressText = "",
    thumbnailUrl = this.cover_url,
    taskId = this.song_id + preferencesHash,
)
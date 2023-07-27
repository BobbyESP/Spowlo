package com.bobbyesp.spowlo.features.media_player

import com.bobbyesp.spowlo.data.local.model.Album
import com.bobbyesp.spowlo.data.local.model.Artist
import com.bobbyesp.spowlo.data.local.model.Folder
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    val songs: Flow<List<Song>>
    val artists: Flow<List<Artist>>
    val albums: Flow<List<Album>>
    val folders: Flow<List<Folder>>
}
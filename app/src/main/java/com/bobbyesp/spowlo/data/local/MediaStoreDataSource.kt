package com.bobbyesp.spowlo.data.local

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.bobbyesp.spowlo.data.model.SortBy
import com.bobbyesp.spowlo.data.model.SortOrder
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import com.bobbyesp.spowlo.utils.mediastore.asFolder
import com.bobbyesp.spowlo.utils.mediastore.buildMediaStoreSortOrder
import com.bobbyesp.spowlo.utils.mediastore.observe
import kotlinx.coroutines.flow.map
import okhttp3.internal.buildList
import javax.inject.Inject

class MediaStoreDataSource @Inject constructor(private val contentResolver: ContentResolver) {
    fun getSongs(
        sortOrder: SortOrder,
        sortBy: SortBy,
        favoriteSongs: Set<String>,
        excludedFolders: List<String>
    ) = contentResolver.observe(uri = MediaStoreConfig.Song.Collection).map {
        buildList {
            contentResolver.query(
                MediaStoreConfig.Song.Collection,
                MediaStoreConfig.Song.Projection,
                buildString {
                    append("${MediaStore.Audio.Media.IS_MUSIC} != 0")
                    repeat(excludedFolders.size) {
                        append(" AND ${MediaStore.Audio.Media.DATA} NOT LIKE ?")
                    }
                },
                excludedFolders.map { "%$it%" }.toTypedArray(),
                buildMediaStoreSortOrder(sortOrder, sortBy)
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val artistId = cursor.getLong(artistIdColumn)
                    val albumId = cursor.getLong(albumIdColumn)
                    val title = cursor.getString(titleColumn)
                    val artist = cursor.getString(artistColumn)
                    val album = cursor.getString(albumColumn)
                    val duration = cursor.getDouble(durationColumn)
                    val date = cursor.getLong(dateColumn)

                    val mediaId = id.toString()
                    val mediaUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val folder = cursor.getString(pathColumn).asFolder()

                    val song = Song(id, title, artist, album, mediaUri, duration, folder)
                    add(song)
                }
            }
        }
    }
}

internal object MediaStoreConfig {
    object Song {
        val Collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        val Projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )
    }
}
package com.bobbyesp.spowlo.features.lyrics_downloader.data.local

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.bobbyesp.spowlo.BuildConfig
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song

object MediaStoreReceiver {

    /**
     * This function returns a list of all the songs in the device.
     * @param applicationContext The application context.
     * @return A list of all the songs in the device.
     */
    fun getAllSongsFromMediaStore(applicationContext: Context): List<Song> {
        val contentResolver: ContentResolver = applicationContext.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        if(BuildConfig.DEBUG) {
            println("MediaStoreReceiver.getAllSongsFromMediaStore: uri = $uri")
        }
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val songs = mutableListOf<Song>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            // Here we can get more data from the songs, like the album cover, the year, etc...
        )
        val sortOrder = MediaStore.Audio.Media.TITLE // Sort ascending by title

        contentResolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val duration = cursor.getDouble(durationColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val path = cursor.getString(pathColumn)

                val songArtworkUri = Uri.parse("content://media/external/audio/albumart")
                val imgUri = ContentUris.withAppendedId(
                    songArtworkUri,
                    albumId
                )

                val song = Song(id, title, artist, album, imgUri, duration, path)
                songs.add(song)
            }
        }
        return songs
    }

    fun getAllSongsFromMediaStore(applicationContext: Context, searchTerm: String?, filterType: MediaStoreFilterType?): List<Song> {
        val contentResolver: ContentResolver = applicationContext.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        if (BuildConfig.DEBUG) {
            println("MediaStoreReceiver.getAllSongsFromMediaStore: uri = $uri")
        }

        val selection = if (!searchTerm.isNullOrEmpty() && filterType != null) {
            when(filterType) {
                MediaStoreFilterType.TITLE -> "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.TITLE} LIKE '%$searchTerm%'"
                MediaStoreFilterType.ARTIST -> "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.ARTIST} LIKE '%$searchTerm%'"
            }
        } else {
            MediaStore.Audio.Media.IS_MUSIC + " != 0"
        }

        val songs = mutableListOf<Song>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val sortOrder = MediaStore.Audio.Media.TITLE // Sort ascending by title

        contentResolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val duration = cursor.getDouble(durationColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val path = cursor.getString(pathColumn)

                val songArtworkUri = Uri.parse("content://media/external/audio/albumart")
                val imgUri = ContentUris.withAppendedId(
                    songArtworkUri,
                    albumId
                )

                val song = Song(id, title, artist, album, imgUri, duration, path)
                songs.add(song)
            }
        }

        return songs
    }

}

enum class MediaStoreFilterType {
    TITLE,
    ARTIST,
}
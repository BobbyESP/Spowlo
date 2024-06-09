package com.bobbyesp.utilities.mediastore

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import com.bobbyesp.model.Song
import com.bobbyesp.utilities.R
import com.bobbyesp.utilities.mediastore.advanced.advancedQuery
import com.bobbyesp.utilities.mediastore.advanced.observe
import kotlinx.coroutines.flow.map
import java.io.FileNotFoundException

object MediaStoreReceiver {

    /**
     * This function returns a list of all the songs in the device.
     * @param applicationContext The application context.
     * @return A list of all the songs in the device.
     */
    fun getSongsBySearchTerm(applicationContext: Context): List<Song> {
        val contentResolver: ContentResolver = applicationContext.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

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
                val fileName = path.substring(path.lastIndexOf("/") + 1)

                val songArtworkUri = Uri.parse("content://media/external/audio/albumart")
                val imgUri = ContentUris.withAppendedId(
                    songArtworkUri, albumId
                )

                val song = Song(id, title, artist, album, imgUri, duration, path, fileName)
                songs.add(song)
            }
        }
        return songs
    }

    fun getSongsBySearchTerm(
        applicationContext: Context, searchTerm: String?, filterType: MediaStoreFilterType?
    ): List<Song> {
        val contentResolver: ContentResolver = applicationContext.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val selection = if (!searchTerm.isNullOrEmpty() && filterType != null) {
            when (filterType) {
                MediaStoreFilterType.TITLE -> "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.TITLE} LIKE '%$searchTerm%'"
                MediaStoreFilterType.ARTIST -> "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.ARTIST} LIKE '%$searchTerm%'"
            }
        } else if (!searchTerm.isNullOrEmpty() && filterType == null) {
            "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.TITLE} LIKE '%$searchTerm%'" + " OR ${MediaStore.Audio.Media.ARTIST} LIKE '%$searchTerm%'"
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
                val fileName = path.substring(path.lastIndexOf("/") + 1)

                val songArtworkUri = Uri.parse("content://media/external/audio/albumart")
                val imgUri = ContentUris.withAppendedId(
                    songArtworkUri, albumId
                )

                val song = Song(id, title, artist, album, imgUri, duration, path, fileName)
                songs.add(song)
            }
        }

        return songs
    }

    @SuppressLint("Range")
    fun getFileDescriptorFromPath(
        context: Context, filePath: String, mode: String = "r"
    ): ParcelFileDescriptor? {
        val resolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(MediaStore.Files.FileColumns._ID)
        val selection = "${MediaStore.Files.FileColumns.DATA}=?"
        val selectionArgs = arrayOf(filePath)

        resolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val fileId: Int =
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                if (fileId == -1) {
                    return null
                } else {
                    val fileUri: Uri = Uri.withAppendedPath(uri, fileId.toString())
                    try {
                        return resolver.openFileDescriptor(fileUri, mode)
                    } catch (e: FileNotFoundException) {
                        Log.e("MediaStoreReceiver", "File not found: ${e.message}")
                    }
                }
            }
        }

        return null
    }

    object Advanced {
        suspend fun ContentResolver.getSongs(
            searchTerm: String? = null, filterType: MediaStoreFilterType? = null
        ): List<Song> {
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            val selection = if (!searchTerm.isNullOrEmpty() && filterType != null) {
                when (filterType) {
                    MediaStoreFilterType.TITLE -> "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.TITLE} LIKE '%$searchTerm%'"
                    MediaStoreFilterType.ARTIST -> "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.ARTIST} LIKE '%$searchTerm%'"
                }
            } else if (!searchTerm.isNullOrEmpty() && filterType == null) {
                "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.TITLE} LIKE '%$searchTerm%'" + " OR ${MediaStore.Audio.Media.ARTIST} LIKE '%$searchTerm%'"
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

            advancedQuery(uri, projection, selection, order = sortOrder)?.use { cursor ->
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
                    val fileName = path.substring(path.lastIndexOf("/") + 1)

                    val songArtworkUri = Uri.parse("content://media/external/audio/albumart")
                    val imgUri = ContentUris.withAppendedId(
                        songArtworkUri, albumId
                    )

                    val song = Song(id, title, artist, album, imgUri, duration, path, fileName)
                    songs.add(song)
                }
            }
            return songs
        }

        fun ContentResolver.observeSongs(
            searchTerm: String? = null,
            filter: MediaStoreFilterType? = null,
        ) = observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI).map {
            getSongs(filterType = filter, searchTerm = searchTerm)
        }
    }
}

enum class MediaStoreFilterType {
    TITLE, ARTIST;

    fun toString(context: Context): String {
        return when (this) {
            TITLE -> context.getString(R.string.title)
            ARTIST -> context.getString(R.string.artist)
        }
    }
}
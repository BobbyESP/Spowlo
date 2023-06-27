package com.bobbyesp.spowlo.features.lyrics_downloader.data.local

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song

object MediaStoreReceiver {

    fun getAllSongsFromMediaStore(applicationContext: Context): List<Song> {
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
            MediaStore.Audio.Media.DATA
            // Here we can get more data from the songs, like the album cover, the year, etc...
        )
        val sortOrder = MediaStore.Audio.Media.TITLE + "ASC" // Sort ascending by title

        contentResolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)



            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val duration = cursor.getInt(durationColumn)
                val albumArt = getSongAlbumArt(id, applicationContext)
                val path = cursor.getString(pathColumn)

                val song = Song(id, title, artist, album, albumArt, duration, path)
                songs.add(song)
            }
        }
        return songs
    }

    private fun getSongAlbumArt(songId: Long, applicationContext: Context): Bitmap? {
        val contentResolver: ContentResolver = applicationContext.contentResolver
        val albumArtUri = Uri.parse("content://media/external/audio/albumart")
        val cursor = contentResolver.query(albumArtUri, null, "album_id = $songId", null, null)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return contentResolver.loadThumbnail(albumArtUri, Size(640, 640), null)
        } else {
            cursor?.use { lambdaCursor ->
                if (lambdaCursor.moveToFirst()) {
                    val albumArtColumn = lambdaCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)
                    val albumArtPath = lambdaCursor.getString(albumArtColumn)
                    if (albumArtPath != null) {
                        return BitmapFactory.decodeFile(albumArtPath)
                    }
                }
            }
        }

        return null
    }

}
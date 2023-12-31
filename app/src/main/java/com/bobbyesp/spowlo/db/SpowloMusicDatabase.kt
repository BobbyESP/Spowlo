package com.bobbyesp.spowlo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bobbyesp.spowlo.db.dao.AlbumDao
import com.bobbyesp.spowlo.db.dao.ArtistDao
import com.bobbyesp.spowlo.db.dao.EventDao
import com.bobbyesp.spowlo.db.dao.FormatDao
import com.bobbyesp.spowlo.db.dao.OtherDao
import com.bobbyesp.spowlo.db.dao.PlaylistDao
import com.bobbyesp.spowlo.db.dao.SearchDao
import com.bobbyesp.spowlo.db.dao.SongDao
import com.bobbyesp.spowlo.db.entity.Event
import com.bobbyesp.spowlo.db.entity.LyricsEntity
import com.bobbyesp.spowlo.db.entity.RelatedSongMap
import com.bobbyesp.spowlo.db.entity.SearchHistory
import com.bobbyesp.spowlo.db.entity.album.AlbumArtistMap
import com.bobbyesp.spowlo.db.entity.album.AlbumEntity
import com.bobbyesp.spowlo.db.entity.artist.ArtistEntity
import com.bobbyesp.spowlo.db.entity.format.FormatEntity
import com.bobbyesp.spowlo.db.entity.playlist.PlaylistEntity
import com.bobbyesp.spowlo.db.entity.playlist.PlaylistSongMap
import com.bobbyesp.spowlo.db.entity.playlist.PlaylistSongMapPreview
import com.bobbyesp.spowlo.db.entity.song.SongAlbumMap
import com.bobbyesp.spowlo.db.entity.song.SongArtistMap
import com.bobbyesp.spowlo.db.entity.song.SongEntity
import com.bobbyesp.spowlo.db.entity.song.SortedSongAlbumMap
import com.bobbyesp.spowlo.db.entity.song.SortedSongArtistMap

@Database(
    entities = [
        SongEntity::class,
        ArtistEntity::class,
        AlbumEntity::class,
        PlaylistEntity::class,
        SongArtistMap::class,
        SongAlbumMap::class,
        AlbumArtistMap::class,
        PlaylistSongMap::class,
        SearchHistory::class,
        FormatEntity::class,
        LyricsEntity::class,
        Event::class,
        RelatedSongMap::class
    ],
    views = [
        SortedSongArtistMap::class,
        SortedSongAlbumMap::class,
        PlaylistSongMapPreview::class
    ],
    version = 1,
    exportSchema = true,
    autoMigrations = []
)
@TypeConverters(DbConverters::class)
abstract class SpowloMusicDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun artistDao(): ArtistDao
    abstract fun albumDao(): AlbumDao
    abstract fun searchDao(): SearchDao
    abstract fun eventDao(): EventDao
    abstract fun formatDao(): FormatDao
    abstract fun otherDao(): OtherDao

    companion object {
        const val DB_NAME = "spowlo_general_database.db"
    }
}
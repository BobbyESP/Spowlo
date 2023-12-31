package com.bobbyesp.spowlo.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.bobbyesp.spowlo.db.dao.common.BaseDao
import com.bobbyesp.spowlo.db.entity.Album
import com.bobbyesp.spowlo.db.entity.Artist
import com.bobbyesp.spowlo.db.entity.Playlist
import com.bobbyesp.spowlo.db.entity.SearchHistory
import com.bobbyesp.spowlo.db.entity.Song
import com.zionhuang.innertube.models.SongItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao : BaseDao<SongItem> {
    @Transaction
    @Query("SELECT * FROM song WHERE title LIKE '%' || :query || '%' AND inLibrary IS NOT NULL LIMIT :previewSize")
    fun searchSongs(query: String, previewSize: Int = Int.MAX_VALUE): Flow<List<Song>>

    @Transaction
    @Query("SELECT *, (SELECT COUNT(1) FROM song_artist_map JOIN song ON song_artist_map.songId = song.id WHERE artistId = artist.id AND song.inLibrary IS NOT NULL) AS songCount FROM artist WHERE name LIKE '%' || :query || '%'  AND bookmarkedAt IS NOT NULL LIMIT :previewSize")
    fun searchArtists(query: String, previewSize: Int = Int.MAX_VALUE): Flow<List<Artist>>

    @Transaction
    @Query("SELECT * FROM album WHERE title LIKE '%' || :query || '%' AND EXISTS(SELECT * FROM song WHERE song.albumId = album.id AND song.inLibrary IS NOT NULL) LIMIT :previewSize")
    fun searchAlbums(query: String, previewSize: Int = Int.MAX_VALUE): Flow<List<Album>>

    @Transaction
    @Query("SELECT *, (SELECT COUNT(*) FROM playlist_song_map WHERE playlistId = playlist.id) AS songCount FROM playlist WHERE name LIKE '%' || :query || '%' LIMIT :previewSize")
    fun searchPlaylists(query: String, previewSize: Int = Int.MAX_VALUE): Flow<List<Playlist>>

    @Query("SELECT * FROM search_history WHERE `query` LIKE :query || '%' ORDER BY id DESC")
    fun searchHistory(query: String = ""): Flow<List<SearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searchHistory: SearchHistory)

    @Query("DELETE FROM search_history")
    fun clearSearchHistory()
}
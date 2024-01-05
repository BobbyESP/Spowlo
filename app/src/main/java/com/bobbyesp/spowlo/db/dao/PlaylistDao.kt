package com.bobbyesp.spowlo.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.bobbyesp.spowlo.db.dao.common.BaseDao
import com.bobbyesp.spowlo.db.entity.Playlist
import com.bobbyesp.spowlo.db.entity.playlist.PlaylistSong
import com.bobbyesp.spowlo.db.entity.playlist.PlaylistSongMap
import com.bobbyesp.utilities.audio.model.PlaylistSortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface PlaylistDao : BaseDao<Playlist> {
    @Transaction
    @Query("SELECT * FROM playlist_song_map WHERE playlistId = :playlistId ORDER BY position")
    fun playlistSongs(playlistId: String): Flow<List<PlaylistSong>>

    @Transaction
    @Query("SELECT *, (SELECT COUNT(*) FROM playlist_song_map WHERE playlistId = playlist.id) AS songCount FROM playlist ORDER BY rowId")
    fun playlistsByCreateDateAsc(): Flow<List<Playlist>>

    @Transaction
    @Query("SELECT *, (SELECT COUNT(*) FROM playlist_song_map WHERE playlistId = playlist.id) AS songCount FROM playlist ORDER BY name")
    fun playlistsByNameAsc(): Flow<List<Playlist>>

    @Transaction
    @Query("SELECT *, (SELECT COUNT(*) FROM playlist_song_map WHERE playlistId = playlist.id) AS songCount FROM playlist ORDER BY songCount")
    fun playlistsBySongCountAsc(): Flow<List<Playlist>>

    fun playlists(sortType: PlaylistSortType, descending: Boolean) =
        when (sortType) {
            PlaylistSortType.CREATE_DATE -> playlistsByCreateDateAsc()
            PlaylistSortType.NAME -> playlistsByNameAsc()
            PlaylistSortType.SONG_COUNT -> playlistsBySongCountAsc()
        }.map {
            if (descending) it.reversed() else it
        }

    @Transaction
    @Query("SELECT *, (SELECT COUNT(*) FROM playlist_song_map WHERE playlistId = playlist.id) AS songCount FROM playlist WHERE id = :playlistId")
    fun playlist(playlistId: String): Flow<Playlist?>

    @Query(
        """
        UPDATE playlist_song_map SET position = 
            CASE 
                WHEN position < :fromPosition THEN position + 1
                WHEN position > :fromPosition THEN position - 1
                ELSE :toPosition
            END 
        WHERE playlistId = :playlistId AND position BETWEEN MIN(:fromPosition, :toPosition) AND MAX(:fromPosition, :toPosition)
    """
    )
    fun move(playlistId: String, fromPosition: Int, toPosition: Int)

    @Query("DELETE FROM playlist_song_map WHERE playlistId = :playlistId")
    fun clearPlaylist(playlistId: String)

    @Query("SELECT * FROM playlist_song_map WHERE songId = :songId")
    fun playlistSongMaps(songId: String): List<PlaylistSongMap>

    @Query("SELECT * FROM playlist_song_map WHERE playlistId = :playlistId AND position >= :from ORDER BY position")
    fun playlistSongMaps(playlistId: String, from: Int): List<PlaylistSongMap>
}
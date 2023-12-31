package com.bobbyesp.spowlo.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.bobbyesp.spowlo.db.dao.common.BaseDao
import com.bobbyesp.spowlo.db.entity.Song
import com.bobbyesp.utilities.audio.model.SongSortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface SongDao: BaseDao<Song> {
    @Transaction
    @Query("SELECT * FROM song WHERE inLibrary IS NOT NULL ORDER BY rowId")
    fun songsByRowIdAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE inLibrary IS NOT NULL ORDER BY inLibrary")
    fun songsByCreateDateAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE inLibrary IS NOT NULL ORDER BY title")
    fun songsByNameAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE inLibrary IS NOT NULL ORDER BY totalPlayTime")
    fun songsByPlayTimeAsc(): Flow<List<Song>>

    fun songs(sortType: SongSortType, descending: Boolean) =
        when (sortType) {
            SongSortType.CREATE_DATE -> songsByCreateDateAsc()
            SongSortType.NAME -> songsByNameAsc()
            SongSortType.ARTIST -> songsByRowIdAsc().map { songs ->
                songs.sortedBy { song ->
                    song.artists.joinToString(separator = "") { it.name }
                }
            }

            SongSortType.PLAY_TIME -> songsByPlayTimeAsc()
        }.map {
            if(descending) it.reversed() else it
        }

    @Transaction
    @Query("SELECT * FROM song WHERE liked ORDER BY rowId")
    fun likedSongsByRowIdAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE liked ORDER BY inLibrary")
    fun likedSongsByCreateDateAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE liked ORDER BY title")
    fun likedSongsByNameAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE liked ORDER BY totalPlayTime")
    fun likedSongsByPlayTimeAsc(): Flow<List<Song>>

    fun likedSongs(sortType: SongSortType, descending: Boolean) =
        when (sortType) {
            SongSortType.CREATE_DATE -> likedSongsByCreateDateAsc()
            SongSortType.NAME -> likedSongsByNameAsc()
            SongSortType.ARTIST -> likedSongsByRowIdAsc().map { songs ->
                songs.sortedBy { song ->
                    song.artists.joinToString(separator = "") { it.name }
                }
            }

            SongSortType.PLAY_TIME -> likedSongsByPlayTimeAsc()
        }.map {
            if(descending) it.reversed() else it
        }

    @Query("SELECT COUNT(1) FROM song WHERE liked")
    fun likedSongsCount(): Flow<Int>
}


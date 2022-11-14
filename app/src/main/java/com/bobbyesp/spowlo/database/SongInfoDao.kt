package com.bobbyesp.spowlo.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SongInfoDao {
    @Insert
    suspend fun insertAll(vararg info: DownloadedSongInfo)

    @Query("select * from DownloadedSongInfo")
    fun getAllMedia(): Flow<List<DownloadedSongInfo>>

    @Query("select * from DownloadedSongInfo where id=:id")
    suspend fun getInfoById(id: Int): DownloadedSongInfo

    @Delete
    suspend fun delete(info: DownloadedSongInfo)

    @Query("DELETE FROM DownloadedSongInfo WHERE id = :id")
    suspend fun deleteInfoById(id: Int)

    @Query("DELETE FROM DownloadedSongInfo WHERE songPath = :path")
    suspend fun deleteInfoByPath(path: String)

    @Query("SELECT * FROM CommandTemplate")
    fun getTemplateFlow(): Flow<List<CommandTemplate>>

    @Query("SELECT * FROM CommandTemplate")
    suspend fun getTemplateList(): List<CommandTemplate>

    @Insert
    suspend fun insertTemplate(template: CommandTemplate)

    @Update
    suspend fun updateTemplate(template: CommandTemplate)

    @Delete
    suspend fun deleteTemplate(template: CommandTemplate)

    @Query("SELECT * FROM CommandTemplate where id = :id")
    suspend fun getTemplateById(id: Int): CommandTemplate
}
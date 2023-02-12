package com.bobbyesp.spowlo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.bobbyesp.spowlo.utils.FilesUtil
import kotlinx.coroutines.flow.Flow

@Dao
interface SongsInfoDao {
    @Insert
    suspend fun insertAll(vararg info: DownloadedSongInfo)

    @Query("SELECT * FROM DownloadedSongInfo")
    fun getAllMedia(): Flow<List<DownloadedSongInfo>>

    @Query("SELECT * from DownloadedSongInfo WHERE id=:id")
    suspend fun getInfoById(id: Int): DownloadedSongInfo

    @Query("DELETE FROM DownloadedSongInfo WHERE id = :id")
    suspend fun deleteInfoById(id: Int)

    @Query("DELETE FROM DownloadedSongInfo WHERE songPath = :path")
    suspend fun deleteInfoByPath(path: String)

    @Query("SELECT * FROM DownloadedSongInfo WHERE songPath = :path")
    suspend fun getInfoByPath(path: String): DownloadedSongInfo?

    @Transaction
    suspend fun deleteInfoByPathAndInsert(
        songInfo: DownloadedSongInfo,
        path: String = songInfo.songPath
    ) {
        deleteInfoByPath(path)
        insertAll(songInfo)
    }

    @Transaction
    suspend fun insertInfoDistinctByPath(
        songInfo: DownloadedSongInfo,
        path: String = songInfo.songPath
    ) {
        if (getInfoByPath(path) == null)
            insertAll(songInfo)
    }

    @Delete
    suspend fun deleteInfo(vararg info: DownloadedSongInfo)

    @Transaction
    suspend fun deleteInfoListByIdList(idList: List<Int>, deleteFile: Boolean = false) {
        idList.forEach { id ->
            val info = getInfoById(id)
            if (deleteFile) FilesUtil.deleteFile(info.songPath)
            deleteInfo(info)
        }
    }

    @Query("SELECT * FROM CommandTemplate")
    fun getTemplateFlow(): Flow<List<CommandTemplate>>

    @Query("SELECT * FROM CommandTemplate")
    suspend fun getTemplateList(): List<CommandTemplate>

    @Query("select * from CookieProfile")
    fun getCookieProfileFlow(): Flow<List<CookieProfile>>

    @Insert
    suspend fun insertTemplate(template: CommandTemplate): Long

    @Insert
    @Transaction
    suspend fun importTemplates(templateList: List<CommandTemplate>)

    @Update
    suspend fun updateTemplate(template: CommandTemplate)

    @Delete
    suspend fun deleteTemplate(template: CommandTemplate)

    @Query("SELECT * FROM CommandTemplate where id = :id")
    suspend fun getTemplateById(id: Int): CommandTemplate

    @Query("select * from CookieProfile where id=:id")
    suspend fun getCookieById(id: Int): CookieProfile?

    @Update
    suspend fun updateCookieProfile(cookieProfile: CookieProfile)

    @Delete
    suspend fun deleteCookieProfile(cookieProfile: CookieProfile)

    @Insert
    suspend fun insertCookieProfile(cookieProfile: CookieProfile)

    @Query("delete from CommandTemplate where id=:id")
    suspend fun deleteTemplateById(id: Int)

    @Query("select * from CommandShortcut")
    fun getCommandShortcuts(): Flow<List<CommandShortcut>>

    @Query("select * from CommandShortcut")
    suspend fun getShortcutList(): List<CommandShortcut>

    @Delete
    suspend fun deleteShortcut(commandShortcut: CommandShortcut)

    @Insert
    suspend fun insertShortcut(commandShortcut: CommandShortcut): Long

    @Transaction
    @Insert
    suspend fun insertAllShortcuts(shortcuts: List<CommandShortcut>)
}
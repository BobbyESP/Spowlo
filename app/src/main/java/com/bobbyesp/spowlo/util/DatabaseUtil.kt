package com.bobbyesp.spowlo.util

import androidx.room.Room
import com.bobbyesp.spowlo.Spowlo.Companion.applicationScope
import com.bobbyesp.spowlo.Spowlo.Companion.context
import com.bobbyesp.spowlo.database.AppDatabase
import com.bobbyesp.spowlo.database.CommandTemplate
import com.bobbyesp.spowlo.database.DownloadedSongInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object DatabaseUtil {
    val format = Json { prettyPrint = true }
    private const val DATABASE_NAME = "app_database"
    private val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, DATABASE_NAME
    ).build()
    private val dao = db.songInfoDao()
    fun insertInfo(vararg infoList: DownloadedSongInfo) {
        applicationScope.launch(Dispatchers.IO) {
            for (info in infoList) {
                dao.deleteInfoByPath(info.songPath)
                dao.insertAll(info)
            }
        }
    }

    fun getMediaInfo() = dao.getAllMedia()

    fun getTemplateFlow() = dao.getTemplateFlow()

    suspend fun getTemplateList() = dao.getTemplateList()

    suspend fun getInfoById(id: Int): DownloadedSongInfo = dao.getInfoById(id)
    suspend fun deleteInfoById(id: Int) = dao.deleteInfoById(id)

    suspend fun insertTemplate(commandTemplate: CommandTemplate) {
        dao.insertTemplate(commandTemplate)
    }

    suspend fun updateTemplate(commandTemplate: CommandTemplate) {
        dao.updateTemplate(commandTemplate)
    }

    suspend fun deleteTemplate(commandTemplate: CommandTemplate) {
        dao.deleteTemplate(commandTemplate)
    }

    suspend fun exportTemplatesToJson(): String {
        return format.encodeToString(getTemplateList())
    }

    suspend fun importTemplatesFromJson(json: String): Int {
        val list = getTemplateList()
        var cnt = 0
        try {
            format.decodeFromString<List<CommandTemplate>>(json)
                .forEach {
                    if (!list.contains(it)) {
                        cnt++
                        dao.insertTemplate(it.copy(id = 0))
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cnt
    }

    private const val TAG = "DatabaseUtil"
}
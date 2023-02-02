package com.bobbyesp.spowlo.utils

import androidx.annotation.CheckResult
import com.bobbyesp.library.SpotDL
import com.bobbyesp.library.SpotDLRequest
import com.bobbyesp.library.SpotDLResponse
import com.bobbyesp.library.dto.Song
import com.bobbyesp.spowlo.App.Companion.context
import com.bobbyesp.spowlo.database.DownloadedSongInfo
import com.bobbyesp.spowlo.utils.FilesUtil.getCookiesFile
import com.bobbyesp.spowlo.utils.PreferencesUtil.getString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.UUID

object DownloaderUtil {

    private const val TAG = "DownloaderUtil"

    private val jsonFormat = Json {
        ignoreUnknownKeys = true
    }

    data class DownloadPreferences(
        val extractAudio: Boolean = PreferencesUtil.getValue(EXTRACT_AUDIO),
        val createThumbnail: Boolean = PreferencesUtil.getValue(THUMBNAIL),
        val downloadPlaylist: Boolean = PreferencesUtil.getValue(PLAYLIST),
        val subdirectory: Boolean = PreferencesUtil.getValue(SUBDIRECTORY),
        val customPath: Boolean = PreferencesUtil.getValue(CUSTOM_PATH),
        val outputPathTemplate: String = PreferencesUtil.getOutputPathTemplate(),
        val maxFileSize: String = MAX_FILE_SIZE.getString(),
        val cookies: Boolean = PreferencesUtil.getValue(COOKIES),
        val cookiesContent: String = PreferencesUtil.getCookies(),
        val audioFormat: Int = PreferencesUtil.getAudioFormat(),
        val formatId: String = "",
        val privateMode: Boolean = PreferencesUtil.getValue(PRIVATE_MODE),
        val sdcard: Boolean = PreferencesUtil.getValue(SDCARD_DOWNLOAD),
        val sdcardUri: String = SDCARD_URI.getString()
    )

    //Get a random UUID and return it as a string
    private fun getRandomUUID(): String {
        return UUID.randomUUID().toString()
    }

    @CheckResult
    private fun getSongInfo(
        request: SpotDLRequest,
        id: String = getRandomUUID()
    ): Result<List<Song>> =
        request.addOption("--save-file", "$id.spotdl").runCatching {
            val response: SpotDLResponse = SpotDL.getInstance().execute(request, null, null)
            jsonFormat.decodeFromString(response.output)
        }

    private fun SpotDLRequest.addCookies(): SpotDLRequest = this.apply {
        PreferencesUtil.getCookies().run {
            if (isNotEmpty()) {
                addOption(
                    "--cookie-file", FilesUtil.writeContentToFile(
                        this, context.getCookiesFile()
                    ).absolutePath
                )
            }
        }
    }

    @CheckResult
    private fun scanVideoIntoDownloadHistory(
        songInfo: Song,
        downloadPath: String,
    ): List<String> = FilesUtil.scanFileToMediaLibraryPostDownload(
        title = songInfo.song_id, downloadDir = downloadPath
    ).apply {
        insertInfoIntoDownloadHistory(songInfo, this)
    }

    private fun insertInfoIntoDownloadHistory(
        songInfo: Song,
        filePaths: List<String>
    ){
        filePaths.forEach { filePath ->
            DatabaseUtil.insertInfo(
                DownloadedSongInfo(
                    id = 0,
                    songName = songInfo.name,
                    songAuthor = songInfo.artist,
                    songUrl = songInfo.url,
                    thumbnailUrl = songInfo.cover_url,
                    songPath = filePath,
                    extractor = "Youtube Music",
                )
            )
        }
    }
}
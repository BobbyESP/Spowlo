package com.bobbyesp.spowlo.utils

import androidx.annotation.CheckResult
import com.bobbyesp.library.SpotDL
import com.bobbyesp.library.SpotDLRequest
import com.bobbyesp.library.SpotDLResponse
import com.bobbyesp.library.dto.Song
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
    private fun getVideoInfo(request: SpotDLRequest, id: String = getRandomUUID()): Result<List<Song>> =
        request.addOption("--save-file", "$id.spotdl").runCatching {
            val response: SpotDLResponse = SpotDL.getInstance().execute(request, null, null)
            jsonFormat.decodeFromString(response.output)
        }
}
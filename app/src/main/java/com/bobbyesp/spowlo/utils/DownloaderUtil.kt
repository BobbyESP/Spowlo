package com.bobbyesp.spowlo.utils

import android.util.Log
import androidx.annotation.CheckResult
import com.bobbyesp.library.SpotDL
import com.bobbyesp.library.SpotDLRequest
import com.bobbyesp.library.SpotDLResponse
import com.bobbyesp.library.dto.Song
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.App.Companion.context
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.database.CommandTemplate
import com.bobbyesp.spowlo.database.DownloadedSongInfo
import com.bobbyesp.spowlo.utils.FilesUtil.getCookiesFile
import com.bobbyesp.spowlo.utils.FilesUtil.getSdcardTempDir
import com.bobbyesp.spowlo.utils.FilesUtil.moveFilesToSdcard
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
        request.addOption("--save-file", "/data/user/0/com.bobbyesp.spowlo/files/spotdl/$id.spotdl").runCatching {
            val response: SpotDLResponse = SpotDL.getInstance().execute(request, null, null)
            jsonFormat.decodeFromString(response.output)
        }

    @CheckResult
    fun fetchVideoInfoFromUrl(
        url: String, playlistItem: Int = 0, preferences: DownloadPreferences = DownloadPreferences()
    ): Result<List<Song>> =
        SpotDLRequest().apply {
            //TODO: Add options to the request
            addOption("save", url)
        }.run {
            getSongInfo(this)
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
    fun downloadSong(
        songInfo: Song = Song(),
        playlistUrl: String = "",
        playlistItem: Int = 0,
        taskId: String,
        downloadPreferences: DownloadPreferences,
        progressCallback: ((Float, Long, String) -> Unit)?
    ): Result<List<String>> {
        if (songInfo == Song()) return Result.failure(Throwable(context.getString(R.string.fetch_info_error_msg)))
        with(downloadPreferences) {
            val url = playlistUrl.ifEmpty {
                songInfo.url
                    ?: return Result.failure(Throwable(context.getString(R.string.fetch_info_error_msg)))
            }
            val request = SpotDLRequest()
            val pathBuilder = StringBuilder()

            request.apply {
                addOption("download", url)
                addOption("--output", App.audioDownloadDir)
                addOption("--bitrate", AUDIO_QUALITY.getString())
                addOption("--format", AUDIO_FORMAT.getString())

                for (s in request.buildCommand()) Log.d(TAG, s)
            }.runCatching {
                SpotDL.getInstance().execute(this, taskId, callback = progressCallback)
            }.onFailure { th ->
                return if (th.message?.contains("No such file or directory") == true) {
                    th.printStackTrace()
                    onFinishDownloading(
                        this,
                        songInfo = songInfo,
                        downloadPath = pathBuilder.toString(),
                        sdcardUri = sdcardUri
                    )
                } else Result.failure(th)
            }
            return onFinishDownloading(
                this,
                songInfo = songInfo,
                downloadPath = pathBuilder.toString(),
                sdcardUri = sdcardUri
            )
        }
    }

    private fun onFinishDownloading(
        preferences: DownloadPreferences,
        songInfo: Song,
        downloadPath: String,
        sdcardUri: String
    ): Result<List<String>> = preferences.run {
        if (privateMode) {
            Result.success(emptyList())
        } else if (sdcard) {
            Result.success(
                moveFilesToSdcard(
                    sdcardUri = sdcardUri,
                    tempPath = context.getSdcardTempDir(songInfo.song_id)
                ).apply {
                    insertInfoIntoDownloadHistory(songInfo, this)
                })
        } else {
            Result.success(
                scanVideoIntoDownloadHistory(
                    songInfo = songInfo,
                    downloadPath = downloadPath,
                )
            )
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

    suspend fun executeCommandInBackground(
        url: String,
        template: CommandTemplate = PreferencesUtil.getTemplate()
    ){
        TODO("Not yet implemented")
    }
}
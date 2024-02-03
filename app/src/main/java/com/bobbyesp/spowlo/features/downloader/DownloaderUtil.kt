package com.bobbyesp.spowlo.features.downloader

import androidx.annotation.CheckResult
import com.bobbyesp.library.SpotDL
import com.bobbyesp.library.SpotDLRequest
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.App.Companion.audioDownloadDir
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.downloader.Downloader.onProcessEnded
import com.bobbyesp.spowlo.features.downloader.Downloader.onProcessStarted
import com.bobbyesp.spowlo.features.downloader.Downloader.onTaskFailed
import com.bobbyesp.spowlo.features.downloader.Downloader.onTaskFinished
import com.bobbyesp.spowlo.features.downloader.Downloader.onTaskStarted
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType
import com.bobbyesp.spowlo.ui.ext.clearOutputWithEllipsis
import com.bobbyesp.spowlo.utils.notifications.ToastUtil
import com.bobbyesp.spowlo.utils.preferences.PreferencesStrings.THREADS
import com.bobbyesp.spowlo.utils.preferences.PreferencesUtil
import com.bobbyesp.spowlo.utils.preferences.PreferencesUtil.getInt

object DownloaderUtil {

    val prefs = PreferencesUtil

    data class DownloaderPreferences(
        val threads: Int = THREADS.getInt(),
    )

    fun threadsSelector(itemType: SpotifyItemType): Int {
        return when (itemType) {
            SpotifyItemType.TRACKS -> 1
            else -> THREADS.getInt()
        }
    }

    /**
     * Common request for all download types; this applies all the downloader options provided by the user
     * @param downloadPreferences the preferences of the downloader
     * @param url the url of the song
     * @param request the request to be modified that will be used to download the song
     * @param pathBuilder the path builder that will be used to build the path of the song
     */
    private fun commonDownloadRequest(
        downloadPreferences: DownloaderPreferences,
        url: String,
        request: SpotDLRequest,
        pathBuilder: StringBuilder
    ): SpotDLRequest {
        return with(downloadPreferences) {
            pathBuilder.append(audioDownloadDir)
            request.apply {
                addOption("download", url)
                addOption("--output", pathBuilder.toString())
            }
        }
    }

    @CheckResult
    fun downloadSong(
        //TODO: Rethink parameters
        downloadInfo: Downloader.DownloadInfo? = null,
        taskId: String,
        downloaderPreferences: DownloaderPreferences = DownloaderPreferences(),
    ): Result<List<String>> {
        if (downloadInfo == null) return Result.failure(Exception(App.appContext.getString(R.string.song_info_null)))

        val isMultipleTrack = downloadInfo.type != SpotifyItemType.TRACKS

        val pathBuilder = StringBuilder()
        val request = commonDownloadRequest(
            downloaderPreferences,
            downloadInfo.url,
            SpotDLRequest(),
            pathBuilder
        ).apply {
            //add other options in here
            addOption("--threads", threadsSelector(downloadInfo.type).toString())
        }

        onProcessStarted()
        onTaskStarted(
            downloadInfo = downloadInfo,
            taskName = taskId,
        )
        ToastUtil.makeToastSuspend(
            App.appContext,
            App.appContext.getString(R.string.downloading_song)
        )
        kotlin.runCatching {
            val response = SpotDL.getInstance().execute(
                request = request,
                processId = taskId,
                forceProcessDestroy = true,
                callback = { progress, _, text ->
                    Downloader.updateTaskOutput(
                        taskKey = taskId,
                        currentOutLine = text,
                        progress = progress,
                        isMultipleTrack = isMultipleTrack
                    )
                })
            val finalResponse = response.output.clearOutputWithEllipsis()
            onTaskFinished(taskId, finalResponse, "NOTIFICATION TITLE")
        }.onFailure {
            it.printStackTrace()
            if (it is SpotDL.CancelledException) {
                return Result.failure(Exception(App.appContext.getString(R.string.download_cancelled)))
            }
            it.message.run {
                if (this.isNullOrEmpty()) onTaskFinished(
                    taskId,
                    output = "PLACEHOLDER TEXT: The output of the process was empty."
                )
                else onTaskFailed(taskId, this)
            }
        }
        onProcessEnded()
        return onDownloadFinished(
            downloaderPreferences,
            pathBuilder.toString(),
            null //TODO: Add sdcardUri in downloader preferences
        )
    }

    private fun onDownloadFinished(
        preferences: DownloaderPreferences,
        path: String,
        sdcardUri: String?
    ): Result<List<String>> { //Result<List<String>> return a list of paths where the songs are downloaded, but I don't think we need it
        //TODO: Add scanning to app db and media store
        return Result.success(listOf(path)) // <- this is the result of the download (PLACEHOLDER)
    }
}
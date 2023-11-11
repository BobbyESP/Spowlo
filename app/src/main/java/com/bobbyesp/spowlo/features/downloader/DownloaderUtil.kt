package com.bobbyesp.spowlo.features.downloader

import androidx.annotation.CheckResult
import com.bobbyesp.library.SpotDL
import com.bobbyesp.library.SpotDLRequest
import com.bobbyesp.library.dto.Song
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.utils.notifications.ToastUtil
import com.bobbyesp.spowlo.utils.preferences.PreferencesStrings.THREADS
import com.bobbyesp.spowlo.utils.preferences.PreferencesUtil
import com.bobbyesp.spowlo.utils.preferences.PreferencesUtil.getInt

object DownloaderUtil {

    val prefs = PreferencesUtil

    data class DownloaderPreferences(
        val threads: Int = THREADS.getInt(),
    )

    /**
     * Common request for all download types; this applies all the downloader options provided by the user
     * @param downloadPreferences the preferences of the downloader
     * @param url the url of the song
     * @param request the request to be modified that will be used to download the song
     * @param pathBuilder the path builder that will be used to build the path of the song
     */
    private fun commonRequest(
        downloadPreferences: DownloaderPreferences,
        url: String,
        request: SpotDLRequest,
        pathBuilder: StringBuilder
    ): SpotDLRequest {
        return with(downloadPreferences) {
            request.apply {

            }
        }
    }

    @CheckResult
    fun downloadSong(
        song: Song? = null,
        taskId: String,
        downloaderPreferences: DownloaderPreferences,
        progressCallback: ((Float, Long, String) -> Unit)? = null,
    ): Result<List<String>> {
        if(song == null) return Result.failure(Exception(App.appContext.getString(R.string.song_info_null)))

        val url = song.url
        val request: SpotDLRequest = SpotDLRequest()
        val pathBuilder = StringBuilder()

        with(downloaderPreferences) {
            commonRequest(downloaderPreferences, url, request, pathBuilder).runCatching {
                SpotDL.getInstance().execute(this, taskId, progressCallback)
            }.onSuccess {
                ToastUtil.makeToast(App.appContext, App.appContext.getString(R.string.download_finished))
            }.onFailure {
                ToastUtil.makeToast(App.appContext, it.message ?: App.appContext.getString(R.string.unknown_error))
            }
        }
        return onDownloadFinished(
            downloaderPreferences,
            song,
            pathBuilder.toString(),
            null //TODO: Add sdcardUri in downloader preferences
        )
    }

    private fun onDownloadFinished(
        preferences: DownloaderPreferences,
        song: Song,
        path: String,
        sdcardUri: String?
    ): Result<List<String>> {
       //TODO: Add scanning to app db and media store
        return Result.success(listOf(path)) // <- this is the result of the download (PLACEHOLDER)
    }
}
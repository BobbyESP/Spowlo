package com.bobbyesp.spowlo

import android.util.Log
import androidx.annotation.CheckResult
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.bobbyesp.library.SpotDL
import com.bobbyesp.library.dto.Song
import com.bobbyesp.spowlo.App.Companion.applicationScope
import com.bobbyesp.spowlo.App.Companion.context
import com.bobbyesp.spowlo.database.CommandTemplate
import com.bobbyesp.spowlo.utils.DownloaderUtil
import com.bobbyesp.spowlo.utils.FilesUtil
import com.bobbyesp.spowlo.utils.ToastUtil
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object Downloader {

    sealed class State {
        data class DownloadingPlaylist(
            val currentItem: Int = 0,
            val itemCount: Int = 0,
        ) : State()

        object DownloadingSong : State()
        object FetchingInfo : State()
        object Idle : State()
    }

    fun makeKey(url: String, templateName: String): String = "${templateName}_$url"

    data class CustomCommandTask(
        val template: CommandTemplate,
        val url: String,
        val output: String,
        val state: State,
        val currentLine: String
    ) {
        fun toKey() = makeKey(url, template.name)
        sealed class State {
            data class Error(val errorReport: String) : State()
            object Completed : State()
            object Canceled : State()
            data class Running(val progress: Float) : State()
        }

        override fun hashCode(): Int {
            return (this.url + this.template.name + this.template.template).hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CustomCommandTask

            if (template != other.template) return false
            if (url != other.url) return false
            if (output != other.output) return false
            if (state != other.state) return false
            if (currentLine != other.currentLine) return false

            return true
        }


        fun onCopyLog(clipboardManager: ClipboardManager) {
            clipboardManager.setText(AnnotatedString(output))
        }


        fun onRestart() {
            applicationScope.launch(Dispatchers.IO) {
                DownloaderUtil.executeCommandInBackground(url, template)
            }
        }


        fun onCopyError(clipboardManager: ClipboardManager) {
            clipboardManager.setText(AnnotatedString(currentLine))
            ToastUtil.makeToast(R.string.error_copied)
        }

        fun onCancel() {
            toKey().run {
                SpotDL.getInstance().destroyProcessById(this)
                onProcessCanceled(this)
            }
        }
    }

    data class ErrorState(
        val errorReport: String = "",
        val errorMessageResId: Int = R.string.unknown_error,
    ) {
        fun isErrorOccurred(): Boolean =
            errorMessageResId != R.string.unknown_error || errorReport.isNotEmpty()
    }

    data class DownloadTaskItem(
        val info: Song = Song(),
        val spotifyUrl: String = "",
        val name: String = "",
        val artist: String = "",
        val duration: Double = 0.0,
        val isExplicit: Boolean = false,
        val hasLyrics: Boolean = false,
        // val fileSizeApprox: Long = 0,
        val progress: Float = 0f,
        val progressText: String = "",
        val thumbnailUrl: String = "",
        val taskId: String = "",
        val output: String = "",
        // val playlistIndex: Int = 0,
    )

    private fun Song.toTask(playlistIndex: Int = 0, preferencesHash: Int): DownloadTaskItem =
        DownloadTaskItem(
            info = this,
            spotifyUrl = this.url,
            name = this.name,
            artist = this.artist,
            duration = this.duration,
            isExplicit = this.explicit,
            hasLyrics = this.lyrics.isNullOrEmpty(),
            // fileSizeApprox = this.fileSizeApprox,
            progress = 0f,
            progressText = "",
            thumbnailUrl = this.cover_url,
            taskId = this.song_id + preferencesHash + playlistIndex,
            // playlistIndex = playlistIndex,
        )

    private var currentJob: Job? = null
    private var downloadResultTemp: Result<List<String>> = Result.failure(Exception())

    //DOWNLOADER STATE FLOW
    private val mutableDownloaderState: MutableStateFlow<State> = MutableStateFlow(State.Idle)
    val downloaderState = mutableDownloaderState.asStateFlow()

    //TASK ITEM FLOW
    private val mutableTaskState = MutableStateFlow(DownloadTaskItem())
    val taskState = mutableTaskState.asStateFlow()

    //ERROR STATE FLOW
    private val mutableErrorState = MutableStateFlow(ErrorState())
    val errorState = mutableErrorState.asStateFlow()

    //PROCESSES COUNT
    private val mutableProcessCount = MutableStateFlow(0)
    private val processCount = mutableProcessCount.asStateFlow()

    val mutableTaskList = mutableStateMapOf<String, CustomCommandTask>()


    fun onProcessEnded() =
        mutableProcessCount.update { it - 1 }


    fun onProcessCanceled(taskId: String) =
        mutableTaskList.run {
            get(taskId)?.let {
                this.put(
                    taskId,
                    it.copy(state = CustomCommandTask.State.Canceled)
                )
            }
        }

    fun isDownloaderAvailable(): Boolean {
        if (downloaderState.value !is State.Idle) {
            ToastUtil.makeToastSuspend(context.getString(R.string.task_running))
            return false
        }
        return true
    }

    @CheckResult
    private suspend fun downloadSong(
        songInfo: Song,
        preferences: DownloaderUtil.DownloadPreferences = DownloaderUtil.DownloadPreferences()
    ): Result<List<String>> {

        val isDownloadingPlaylist = downloaderState.value is State.DownloadingPlaylist

        mutableTaskState.update { songInfo.toTask(preferencesHash = preferences.hashCode()) }

        if (!isDownloadingPlaylist) updateState(State.DownloadingSong)
        return DownloaderUtil.downloadSong(
            songInfo = songInfo,
            playlistUrl = "",
            playlistItem = 0,
            downloadPreferences = preferences,
            taskId = songInfo.song_id + preferences.hashCode()
        ) { progress, _, line ->
            Log.d("Downloader", line)
            mutableTaskState.update {
                it.copy(progress = progress, progressText = line)
            }
            /*NotificationUtil.notifyProgress(
                notificationId = notificationId,
                progress = progress.toInt(),
                text = line,
                title = videoInfo.title
            )*/
        }.onFailure {
            if (it is SpotDL.CanceledException) return@onFailure
            Log.d("Downloader", "The download has been canceled (app thread)")
            manageDownloadError(
                it,
                false,
                //notificationId = notificationId,
                isTaskAborted = !isDownloadingPlaylist
            )
        }.onSuccess {
            if (!isDownloadingPlaylist) finishProcessing()
            val text =
                context.getString(if (it.isEmpty()) R.string.status_completed else R.string.download_finish_notification)
            FilesUtil.createIntentForOpeningFile(it.firstOrNull()).run {
                /* NotificationUtil.finishNotification(
                     notificationId,
                     title = videoInfo.title,
                     text = text,
                     intent = if (this != null) PendingIntent.getActivity(
                         context,
                         0,
                         this,
                         PendingIntent.FLAG_IMMUTABLE
                     ) else null
                 )*/
            }
        }
    }

    fun getInfoAndDownload(
        url: String,
        downloadPreferences: DownloaderUtil.DownloadPreferences = DownloaderUtil.DownloadPreferences()
    ) {
        currentJob = applicationScope.launch(Dispatchers.IO) {
            updateState(State.FetchingInfo)
            DownloaderUtil.fetchSongInfoFromUrl(
                url = url,
                preferences = downloadPreferences
            )
                .onFailure {
                    manageDownloadError(
                        it,
                        isFetchingInfo = true,
                        isTaskAborted = true
                    )
                }
                .onSuccess { info ->
                    for (song in info) {
                        downloadResultTemp = downloadSong(
                            songInfo = song,
                            preferences = downloadPreferences
                        )
                    }
                }
        }
    }

    fun getRequestedMetadata(
        url: String,
        downloadPreferences: DownloaderUtil.DownloadPreferences = DownloaderUtil.DownloadPreferences()
    ) {
        currentJob = applicationScope.launch(Dispatchers.IO) {
            updateState(State.FetchingInfo)
            DownloaderUtil.fetchSongInfoFromUrl(
                url = url,
                preferences = downloadPreferences
            )
                .onFailure {
                    manageDownloadError(
                        it,
                        isFetchingInfo = true,
                        isTaskAborted = true
                    )
                }
                .onSuccess { info ->
                    DownloaderUtil.updateSongsState(info)
                    mutableTaskState.update { DownloaderUtil.songsState.value[0].toTask(preferencesHash = downloadPreferences.hashCode()) }
                    finishProcessing()
                }
        }
    }

    fun onTaskStarted(template: CommandTemplate, url: String) =
        CustomCommandTask(
            template = template,
            url = url,
            output = "",
            state = CustomCommandTask.State.Running(0f),
            currentLine = ""
        ).run {
            mutableTaskList.put(this.toKey(), this)
        }

    fun updateTaskOutput(template: CommandTemplate, url: String, line: String, progress: Float) {
        val key = makeKey(url, template.name)
        val oldValue = mutableTaskList[key] ?: return
        val newValue = oldValue.run {
            copy(
                output = output + line + "\n",
                currentLine = line,
                state = CustomCommandTask.State.Running(progress)
            )
        }
        mutableTaskList[key] = newValue
    }

    fun onTaskEnded(
        template: CommandTemplate,
        url: String,
        response: String? = null
    ) {
        val key = makeKey(url, template.name)
        /*NotificationUtil.finishNotification(
            notificationId = key.toNotificationId(),
            title = key,
            text = context.getString(R.string.status_completed),
        )*/
        mutableTaskList.run {
            val oldValue = get(key) ?: return
            val newValue = oldValue.copy(state = CustomCommandTask.State.Completed).run {
                response?.let { copy(output = response) } ?: this
            }
            this[key] = newValue
        }
        FilesUtil.scanDownloadDirectoryToMediaLibrary(App.audioDownloadDir)
    }

    fun onTaskError(errorReport: String, template: CommandTemplate, url: String) =
        mutableTaskList.run {
            val key = makeKey(url, template.name)
            /*NotificationUtil.makeErrorReportNotification(
                notificationId = key.toNotificationId(),
                error = errorReport
            )*/
            val oldValue = mutableTaskList[key] ?: return
            mutableTaskList[key] = oldValue.copy(
                state = CustomCommandTask.State.Error(
                    errorReport
                ), currentLine = errorReport, output = oldValue.output + "\n" + errorReport
            )
        }

    fun updateState(state: State) = mutableDownloaderState.update { state }

    fun clearErrorState() {
        mutableErrorState.update { ErrorState() }
    }

    fun showErrorMessage(resId: Int) {
        ToastUtil.makeToastSuspend(context.getString(resId))
        mutableErrorState.update { ErrorState(errorMessageResId = resId) }
    }

    private fun clearProgressState(isFinished: Boolean) {
        mutableTaskState.update {
            it.copy(
                progress = if (isFinished) 100f else 0f,
                progressText = "",
            )
        }
        if (!isFinished)
            downloadResultTemp = Result.failure(Exception())
    }

    private fun finishProcessing() {
        if (downloaderState.value is State.Idle) return
        mutableTaskState.update {
            it.copy(progress = 100f, progressText = "")
        }
        clearProgressState(isFinished = true)
        updateState(State.Idle)
        clearErrorState()
    }

    /**
     * @param isTaskAborted Determines if the download task is aborted due to the given `Exception`
     */
    fun manageDownloadError(
        th: Throwable,
        isFetchingInfo: Boolean,
        isTaskAborted: Boolean = true,
        notificationId: Int? = null,
    ) {
        if (th is SpotDL.CanceledException) return
        th.printStackTrace()
        val resId =
            if (isFetchingInfo) R.string.fetch_info_error_msg else R.string.download_error_msg
        ToastUtil.makeToastSuspend(context.getString(resId))

        mutableErrorState.update {
            ErrorState(
                errorReport = th.message.toString()
            )
        }
        notificationId?.let {/*
            NotificationUtil.finishNotification(
                notificationId = notificationId,
                text = context.getString(R.string.download_error_msg),
            )*/
        }
        if (isTaskAborted) {
            updateState(State.Idle)
            clearProgressState(isFinished = false)
        }

    }

    fun cancelDownload() {
        ToastUtil.makeToast(context.getString(R.string.task_canceled))
        currentJob?.cancel(CancellationException(context.getString(R.string.task_canceled)))
        updateState(State.Idle)
        clearProgressState(isFinished = false)
        taskState.value.taskId.run {
            SpotDL.getInstance().destroyProcessById(this)
            //NotificationUtil.cancelNotification(this.toNotificationId())
        }
    }

}
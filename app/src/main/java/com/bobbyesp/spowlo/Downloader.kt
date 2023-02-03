package com.bobbyesp.spowlo

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.bobbyesp.library.dto.Song
import com.bobbyesp.spowlo.App.Companion.SpotDl
import com.bobbyesp.spowlo.App.Companion.applicationScope
import com.bobbyesp.spowlo.App.Companion.context
import com.bobbyesp.spowlo.database.CommandTemplate
import com.bobbyesp.spowlo.database.DownloadedSongInfo
import com.bobbyesp.spowlo.utils.DownloaderUtil
import com.bobbyesp.spowlo.utils.FilesUtil
import com.bobbyesp.spowlo.utils.ToastUtil
import kotlinx.coroutines.Dispatchers
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

        object DownloadingVideo : State()
        object FetchingInfo : State()
        object Idle : State()
    }
    fun cancelDownload() {
        TODO("Not yet implemented")
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
                SpotDl.destroyProcessById(this)
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
        val info: List<Song> = listOf(Song()),
        val spotifyUrl: String = "",
        val name: String = "",
        val artist: String = "",
        val duration: Int = 0,
        val isExplicit: Boolean = false,
        val hasLyrics: Boolean = false,
        // val fileSizeApprox: Long = 0,
        val progress: Float = 0f,
        val progressText: String = "",
        val thumbnailUrl: String = "",
        val taskId: String = "",
        // val playlistIndex: Int = 0,
    )

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

}
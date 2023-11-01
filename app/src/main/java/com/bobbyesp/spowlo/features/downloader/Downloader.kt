package com.bobbyesp.spowlo.features.downloader

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.App.Companion.appContext
import com.bobbyesp.spowlo.App.Companion.startKeepUpService
import com.bobbyesp.spowlo.App.Companion.stopKeepUpService
import com.bobbyesp.spowlo.features.downloader.domain.DownloadTask
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType
import com.bobbyesp.spowlo.ui.ext.containsEllipsis
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object Downloader {
    private var currentJob: Job? = null
    private var downloadResultTemp: Result<List<String>> =
        Result.failure(Exception("No result yet"))

    //PROCESSES COUNT
    private val mutableProcessCount = MutableStateFlow(0)
    private val processCount = mutableProcessCount.asStateFlow()

    val mutableTaskList = mutableStateMapOf<String, DownloadTask>()

    init {
        App.applicationScope.launch {
            //if some tasks are running, we need to keep up the service that makes the app not be killed by the system: onStartKeepUpService()
            processCount.collect {
                Log.i("Downloader", "The running processes are now: $it")
                if (it > 0) {
                    startKeepUpService(appContext)
                } else {
                    stopKeepUpService(appContext)
                }
            }
        }
    }

    private fun getProgress(line: String): Float {
        val percent: Float
        //Get the two numbers before an % in the line
        val regex = Regex("(\\d+)%")
        val matchResult = regex.find(line)
        //Log the result
        percent = matchResult?.groupValues?.get(1)?.toFloat() ?: 0f
        //divide percent by 100 to get a value between 0 and 1
        return percent / 100f
    }

    fun makeKey(title: String, artist: String): String {
        return "$title-$artist"
    }

    fun onTaskStarted(
        id: Int? = null,
        itemName: String,
        artist: String,
        type: SpotifyItemType,
        url: String,
        taskName: String,
    ): DownloadTask = DownloadTask(
        id = id,
        url = url,
        taskName = taskName,
        artist = artist,
        title = itemName,
        state = DownloadTask.DownloadState.Running(0f),
        type = type
    ).run {
        val taskKey = this.toKey()
        mutableTaskList[taskKey] = this

        //TODO: ADD NOTIFICATIONS
        this
    }

    fun updateTaskOutput(
        taskKey: String,
        currentOutLine: String,
        progress: Float,
        isPlaylist: Boolean = false
    ) {
        val oldValue = mutableTaskList[taskKey] ?: return
        val newValue = oldValue.run {
            if (currentLine == currentOutLine || currentOutLine.containsEllipsis() || output.contains(
                    currentOutLine
                )
            ) return
            when (isPlaylist) {
                true -> {
                    copy(
                        currentLine = currentOutLine,
                        state = DownloadTask.DownloadState.Running(
                            if (currentOutLine.contains("Total")) {
                                getProgress(currentOutLine)
                            } else {
                                (state as DownloadTask.DownloadState.Running).progress
                            }
                        ),
                        output = output + "\n" + currentOutLine
                    )
                }

                false -> {
                    copy(
                        output = output + "\n" + currentOutLine,
                        currentLine = currentOutLine,
                        state = DownloadTask.DownloadState.Running(progress)
                    )
                }
            }
        }
        mutableTaskList[taskKey] = newValue
    }

    fun onTaskFinished(
        taskKey: String,
        output: String,
        notificationTitle: String? = null
    ) {
        //TODO ADD NOTIFICATIONS
        mutableTaskList.run {
            val oldValue = get(taskKey) ?: return
            val newValue = oldValue.copy(state = DownloadTask.DownloadState.Success).run {
                copy(output = output)
            }
            this[taskKey] = newValue
        }
        //TODO: Add scan download directory for MediaStore
    }

    fun onTaskFailed(errorReport: String, taskKey: String) {
        //TODO ADD NOTIFICATIONS
        mutableTaskList.run {
            val oldValue = get(taskKey) ?: return
            val newValue = oldValue.copy(
                state = DownloadTask.DownloadState.Failed(errorReport),
                currentLine = errorReport,
                output = oldValue.output + "\n" + errorReport
            )
            this[taskKey] = newValue
        }
    }

    fun onProcessStarted() = mutableProcessCount.update { it + 1 }
    fun onProcessEnded() = mutableProcessCount.update { it - 1 }
    fun onProcessCancelled(taskId: String) = mutableTaskList.run {
        get(taskId)?.let {
            this.put(
                taskId,
                it.copy(state = DownloadTask.DownloadState.Cancelled)
            )
        }
    }

}
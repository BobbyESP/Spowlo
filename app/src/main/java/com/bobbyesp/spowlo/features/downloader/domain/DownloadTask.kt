package com.bobbyesp.spowlo.features.downloader.domain

import android.content.Context
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.downloader.Downloader
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType
import com.bobbyesp.spowlo.utils.notifications.ToastUtil
import kotlin.random.Random

data class DownloadTask(
    val id: Int? = Random.nextInt(0, 100000),
    val title: String,
    val artist: String,
    val type: SpotifyItemType,
    val taskName: String = "$title - $artist",
    val url: String,
    val thumbnailUrl: String,
    val output: String = "",
    val currentLine: String? = null,
    val state: DownloadState
) {
    fun toKey() = Downloader.makeKey(title, artist)
    override fun hashCode(): Int {
        return toKey().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DownloadTask

        if (url != other.url) return false
        if (output != other.output) return false
        if (state != other.state) return false
        if (currentLine != other.currentLine) return false

        return true
    }

    fun onCopyOutput(context: Context, clipboardManager: ClipboardManager) {
        clipboardManager.setText(AnnotatedString(output))
        ToastUtil.makeToastSuspend(context, context.getString(R.string.output_copied))
    }

    fun onCopyCurrentLine(context: Context, clipboardManager: ClipboardManager) {
        currentLine?.let {
            clipboardManager.setText(AnnotatedString(it))
            ToastUtil.makeToastSuspend(context, context.getString(R.string.current_line_copied))
        } ?: ToastUtil.makeToastSuspend(context, context.getString(R.string.no_current_line))
    }

    fun onCopyUrl(context: Context, clipboardManager: ClipboardManager) {
        clipboardManager.setText(AnnotatedString(url))
        ToastUtil.makeToastSuspend(context, context.getString(R.string.url_copied))
    }


    sealed class DownloadState {
        data class Running(val progress: Float) : DownloadState()
        data object Success : DownloadState()
        data object Cancelled : DownloadState()
        data class Failed(val error: String) : DownloadState()
        companion object {
            fun DownloadState.toTaskState(): TaskState {
                return when (this) {
                    is Running -> TaskState.RUNNING
                    is Success -> TaskState.SUCCESS
                    is Cancelled -> TaskState.CANCELLED
                    is Failed -> TaskState.FAILED
                }
            }
        }
    }

    fun onCopyError(context: Context, clipboardManager: ClipboardManager) {
        if (state is DownloadState.Failed) {
            clipboardManager.setText(AnnotatedString(state.error))
            ToastUtil.makeToastSuspend(context, context.getString(R.string.error_copied))
        } else {
            ToastUtil.makeToastSuspend(context, context.getString(R.string.no_error))
        }
    }

    fun onRestart() {
        TODO("Not yet implemented")
    }

    fun onCancel() {
        TODO("Not yet implemented")
    }
}

enum class TaskState {
    RUNNING,
    SUCCESS,
    CANCELLED,
    FAILED,
}

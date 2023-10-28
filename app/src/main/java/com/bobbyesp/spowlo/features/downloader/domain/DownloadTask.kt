package com.bobbyesp.spowlo.features.downloader.domain

import android.content.Context
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.bobbyesp.library.dto.Song
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.downloader.Downloader
import com.bobbyesp.spowlo.utils.notifications.ToastUtil

data class DownloadTask(
    val id: Int,
    val title: String,
    val artist: String,
    val album: String,
    val taskName : String = "$title - $artist",
    val url: String,
    val progress: Int,
    val output: String,
    val currentLine: String,
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
        clipboardManager.setText(AnnotatedString(currentLine))
        ToastUtil.makeToastSuspend(context, context.getString(R.string.output_copied))
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
    }

    fun onCopyError(context: Context, clipboardManager: ClipboardManager) {
        if(state is DownloadState.Failed) {
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
        TODO( "Not yet implemented")
    }
}

data class DownloadTaskItem(
    val info: Song = Song(),
    val spotifyUrl: String = "",
    val name: String = "",
    val artist: String = "",
    val duration: Double = 0.0,
    val isExplicit: Boolean = false,
    val hasLyrics: Boolean = false,
    val progress: Float = 0f,
    val progressText: String = "",
    val thumbnailUrl: String = "",
    val taskId: String = "",
    val output: String = "",
)

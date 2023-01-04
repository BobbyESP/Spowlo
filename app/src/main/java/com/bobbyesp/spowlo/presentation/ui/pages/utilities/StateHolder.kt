package com.bobbyesp.spowlo.presentation.ui.pages.utilities

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object StateHolder {
    val mutableDownloaderState = MutableStateFlow(DownloaderState())
    val mutableTaskState = MutableStateFlow(DownloadTaskItem())

    val taskState = mutableTaskState.asStateFlow()
    val downloaderState = mutableDownloaderState.asStateFlow()

    data class DownloaderState constructor(
        val isDownloadError: Boolean = false,
        val errorMessage: String = "",
        val isFetchingInfo: Boolean = false,
        val isProcessRunning: Boolean = false,
        val debugMode: Boolean = false,
        val isDownloadingPlaylist: Boolean = false,
        val downloadItemCount: Int = 0,
        val currentItem: Int = 0,
        val isShowingErrorReport: Boolean = false
    )

    data class DownloadTaskItem(
        val webpageUrl: String = "",
        val title: String = "",
        val duration: Int = 0,
        val fileSizeApprox: Long = 0,
        val progress: Float = 0f,
        val progressText: String = "",
        val thumbnailUrl: String = "",
        val taskId: String = "",
        val playlistIndex: Int = 0,
    )

}
package com.bobbyesp.spowlo.presentation.ui.pages.downloader_page

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class DownloaderViewModel @Inject constructor() : ViewModel() {
    private val mutableStateFlow = MutableStateFlow(DownloaderViewState())
    val stateFlow = mutableStateFlow.asStateFlow()
    private var currentJob: Job? = null

    data class DownloaderViewState(
        val spotUrl: String = "",
        val ytUrl: String = "",
        val progress: Float = 0f,
        val isDownloading: Boolean = false,
        val isCancelled: Boolean = false,
        val songTitle: String = "",
        val songArtist: String = "",
        val isDownloadError: Boolean = false,
        val debugMode: Boolean = false,
        val showDownloadSettingDialog: Boolean = false,
        val downloadingTaskId: String = "",
        val isUrlSharingTriggered: Boolean = false,
    )

    fun updateUrl(url: String, isUrlSharingTriggered: Boolean = false) =
        mutableStateFlow.update {
            it.copy(
                ytUrl = url,
                isUrlSharingTriggered = isUrlSharingTriggered
            )
        }

}
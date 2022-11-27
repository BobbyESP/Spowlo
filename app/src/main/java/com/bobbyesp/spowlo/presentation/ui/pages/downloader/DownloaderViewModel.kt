package com.bobbyesp.spowlo.presentation.ui.pages.downloader

import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.Spowlo.Companion.context
import com.bobbyesp.spowlo.util.Utils
import com.bobbyesp.spowlo.util.api.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class DownloaderViewModel @Inject constructor(
    //private val getBridgeApiResponse: GetAPIResponse
) : ViewModel() {

    private val mutableStateFlow = MutableStateFlow(DownloaderViewState())
    private val mutableTaskState = MutableStateFlow(DownloadTaskItem())
    val stateFlow = mutableStateFlow.asStateFlow()
    val taskState = mutableTaskState.asStateFlow()
    private var currentJob: Job? = null

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

   // private val _state = mutableStateOf(APICallState())
    //val state: State<APICallState> = _state


    data class DownloaderViewState(
        val isDownloading: Boolean = false,
        val isFetchingInfo: Boolean = false,
        val isCancelled: Boolean = false,
        val isDownloadError: Boolean = false,
        val isShowingErrorReport: Boolean = false,
        val errorMessage: String = "",
        val debugMode: Boolean = false,
        val url: String = "",
        val showDownloadSettingDialog: Boolean = false,
        val isUrlSharingTriggered: Boolean = false,
        val loaded: Boolean = false,
        val isError: Boolean = false,
        val isLoading: Boolean = true,
        val drawerState: ModalBottomSheetState = ModalBottomSheetState(
            ModalBottomSheetValue.Hidden,
            isSkipHalfExpanded = true
        ),
    )

    data class DownloadTaskItem(
        val ytUrl: String = "",
        val spotUrl: String = "",
        val songTitle: String = "",
        val songArtist: String = "",
        val progress: Float = 0f,
        val artworkUrl: String = "",
        val taskId: String = "",
        val progressText: String = "",
    )

    fun startDownloadVideo() {
        if (stateFlow.value.url.isBlank()) {
            viewModelScope.launch { showErrorMessage(context.getString(R.string.url_empty)) }
            return
        }
        if (stateFlow.value.url.contains("https://open.spotify.com/track/")) {
            return
        }
    }
    private fun showErrorMessage(s: String) {
        Utils.makeToastSuspend(s)
        mutableTaskState.update {
            it.copy(
                progress = 0f,
                progressText = "",
            )
        }
        mutableStateFlow.update {
            it.copy(
                isDownloadError = true,
                errorMessage = s,
                isDownloading = false,
                isFetchingInfo = false, isShowingErrorReport = false
            )
        }
    }


    fun updateUrl(url: String, isUrlSharingTriggered: Boolean = false){
        Log.d("DownloaderViewModel", "updatedUrl: $url")
        mutableStateFlow.update {
            it.copy(
                url = url,
                isUrlSharingTriggered = isUrlSharingTriggered
            )
        }
    }

    fun hideDialog(scope: CoroutineScope, isDialog: Boolean) {
        scope.launch {
            if (isDialog)
                mutableStateFlow.update { it.copy(showDownloadSettingDialog = false) }
            else
                stateFlow.value.drawerState//.hide()
        }
    }

    fun showDialog(scope: CoroutineScope, isDialog: Boolean) {
        scope.launch {
            if (isDialog)
                mutableStateFlow.update { it.copy(showDownloadSettingDialog = true) }
            else
                stateFlow.value.drawerState//.show()
        }
    }


}
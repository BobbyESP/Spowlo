package com.bobbyesp.spowlo.ui.pages.history

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.database.DownloadedSongInfo
import com.bobbyesp.spowlo.utils.DatabaseUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterialApi::class)
class DownloadsHistoryViewModel : ViewModel() {

    private val _mediaInfoFlow = DatabaseUtil.getMediaInfo()

    private val mediaInfoFlow: Flow<List<DownloadedSongInfo>> =
        _mediaInfoFlow.map { it.reversed() }

    val songsListFlow = mediaInfoFlow

    private val mutableStateFlow = MutableStateFlow(SongsListViewState())
    val stateFlow = mutableStateFlow.asStateFlow()

    val filterSetFlow = _mediaInfoFlow.map { infoList ->
        mutableSetOf<String>().apply {
            infoList.forEach {
                this.add(it.songAuthor)
            }
        }
    }

    data class SongsListViewState(
        val activeFilterIndex: Int = -1
    )

    data class SongDetailViewState(
        val id: Int = 0,
        val title: String = "",
        val author: String = "",
        val url: String = "",
        val artworkUrl: String = "",
        val path: String = "",
        val duration: Double = 0.0,
        val drawerState: ModalBottomSheetState = ModalBottomSheetState(
            ModalBottomSheetValue.Hidden, isSkipHalfExpanded = true,
            density = Density(context = App.context)
        ),
        val showDialog: Boolean = false,
    ) {
        constructor(info: DownloadedSongInfo) : this(
            info.id,
            info.songName,
            info.songAuthor,
            info.songUrl,
            info.thumbnailUrl,
            info.songPath,
            info.songDuration
        )
    }

    private val _detailViewState = MutableStateFlow(SongDetailViewState())
    val detailViewState = _detailViewState.asStateFlow()

    fun clickAuthorFilter(index: Int) {
        if (mutableStateFlow.value.activeFilterIndex == index) mutableStateFlow.update {
            it.copy(
                activeFilterIndex = -1
            )
        }
        else mutableStateFlow.update { it.copy(activeFilterIndex = index) }
    }

    fun hideDrawer(scope: CoroutineScope): Boolean {
        if (_detailViewState.value.drawerState.isVisible) {
            scope.launch {
                _detailViewState.value.drawerState.hide()
            }
            return true
        }
        return false
    }

    fun showDrawer(scope: CoroutineScope, item: DownloadedSongInfo) {
        scope.launch {
            _detailViewState.update {
                SongDetailViewState(item)
            }
            _detailViewState.value.drawerState.show()
        }
    }

    fun showDialog() {
        _detailViewState.update { it.copy(showDialog = true) }
    }

    fun hideDialog() {
        _detailViewState.update { it.copy(showDialog = false) }
    }

    fun removeItem(delete: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                DatabaseUtil.deleteInfoListByIdList(listOf(detailViewState.value.id), delete)
            }
        }
    }
}

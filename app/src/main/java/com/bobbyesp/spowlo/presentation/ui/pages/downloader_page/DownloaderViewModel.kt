package com.bobbyesp.spowlo.presentation.ui.pages.downloader_page

import android.app.Activity
import android.util.Log
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.ViewModel
import com.adamratzman.spotify.auth.implicit.startSpotifyImplicitLoginActivity
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.adamratzman.spotify.notifications.SpotifyBroadcastEventData
import com.bobbyesp.spowlo.data.auth.AuthModel
import com.bobbyesp.spowlo.domain.spotify.web_api.auth.SpotifyImplicitLoginActivityImpl
import com.bobbyesp.spowlo.domain.spotify.web_api.auth.SpotifyPkceLoginActivityImpl
import com.bobbyesp.spowlo.util.Utils.makeToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
        val drawerState: Boolean = false,
        val logged: Boolean = false,
        val loaded: Boolean = false,
        val recentBroadcasts: List<SpotifyBroadcastEventData> = mutableListOf()
        /*val drawerState: ModalBottomSheetState = ModalBottomSheetState(
            ModalBottomSheetValue.Hidden,
            isSkipHalfExpanded = true
        ),*/
    )

    fun setup(){
        currentJob = CoroutineScope(Job()).launch {
            mutableStateFlow.update {
                if(AuthModel.credentialStore.spotifyToken != null){
                    it.copy(logged = true)
                } else {
                    it.copy(logged = false)
                }
            }
            if(AuthModel.credentialStore.spotifyToken == null)
            {
                spotifyPkceLogin()
                Log.d("DownloaderViewModel", "Spotify token is null, relogging")
            }
            mutableStateFlow.update {
                it.copy(loaded = true)
            }
        }
    }

    fun spotifyImplicitLogin(activity: Activity? = null){
        activity?.startSpotifyImplicitLoginActivity<SpotifyImplicitLoginActivityImpl>()
    }

    fun spotifyPkceLogin(activity: Activity? = null){
        activity?.startSpotifyClientPkceLoginActivity(SpotifyPkceLoginActivityImpl::class.java)
    }

    fun updateUrl(url: String, isUrlSharingTriggered: Boolean = false) =
        mutableStateFlow.update {
            it.copy(
                ytUrl = url,
                isUrlSharingTriggered = isUrlSharingTriggered
            )
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
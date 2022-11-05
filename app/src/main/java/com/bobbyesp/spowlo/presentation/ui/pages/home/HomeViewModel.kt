package com.bobbyesp.spowlo.presentation.ui.pages.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobbyesp.spowlo.presentation.ui.components.ArchType
import com.bobbyesp.spowlo.util.CPUInfoUtil
import com.bobbyesp.spowlo.util.VersionsUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import javax.inject.Inject

class HomeViewModel @Inject constructor() : ViewModel() {

    private val mutableStateFlow = MutableStateFlow(HomeViewState())
    val stateFlow = mutableStateFlow.asStateFlow()
    private var currentJob: Job? = null

    data class HomeViewState(
        val regular_versions: Map<String, ArchType> = mutableMapOf(),
        val regular_cloned_versions: Map<String, ArchType> = mutableMapOf(),
        val amoled_versions: Map<String, ArchType> = mutableMapOf(),
        val amoled_cloned_versions: Map<String, ArchType> = mutableMapOf(),
        val cpuArch: String = "",
        val originalSpotifyVersion: String = "",
        val clonedSpotifyVersion: String = "",
        val loaded: Boolean = false
        )

    fun setup(){
        //execute infoCard function, and when it finished, update the loaded value to boolean true
        currentJob?.cancel()
        currentJob = viewModelScope.launch(Dispatchers.IO){
            infoCard()
        }
        mutableStateFlow.update {
            it.copy(loaded = true)
        }
    }

    private fun infoCard(){
        mutableStateFlow.update {
            it.copy(
                originalSpotifyVersion = VersionsUtil.getSpotifyVersion(type = "regular"),
                clonedSpotifyVersion = VersionsUtil.getSpotifyVersion(type = "cloned"),
                cpuArch = CPUInfoUtil.getPrincipalCPUArch()
            )
        }
    }
}
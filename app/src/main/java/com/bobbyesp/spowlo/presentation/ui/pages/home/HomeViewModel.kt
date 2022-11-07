package com.bobbyesp.spowlo.presentation.ui.pages.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobbyesp.spowlo.Spowlo
import com.bobbyesp.spowlo.domain.spotify.model.APICallState
import com.bobbyesp.spowlo.domain.spotify.model.APIResponse
import com.bobbyesp.spowlo.domain.spotify.model.PackagesObject
import com.bobbyesp.spowlo.domain.spotify.use_case.GetAPIResponse
import com.bobbyesp.spowlo.util.CPUInfoUtil
import com.bobbyesp.spowlo.util.DownloadUtil
import com.bobbyesp.spowlo.util.VersionsUtil
import com.bobbyesp.spowlo.util.api.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getApiResponse: GetAPIResponse
    ): ViewModel() {

    private val mutableStateFlow = MutableStateFlow(HomeViewState())
    val stateFlow = mutableStateFlow.asStateFlow()
    private var currentJob: Job? = null

    data class HomeViewState(
        val regular_versions: List<PackagesObject> = mutableListOf(),
        val regular_cloned_versions: List<PackagesObject> = mutableListOf(),
        val amoled_versions: List<PackagesObject> = mutableListOf(),
        val amoled_cloned_versions: List<PackagesObject> = mutableListOf(),
        val cpuArch: String = "",
        val originalSpotifyVersion: String = "",
        val clonedSpotifyVersion: String = "",
        val loaded: Boolean = false,
        val regular_latest_version: String = "",
        )

    private val _state = mutableStateOf(APICallState())
    val state: State<APICallState> = _state


    fun setup(){
        currentJob?.cancel()
        currentJob = viewModelScope.launch{
            infoCard()
        }
        mutableStateFlow.update {
            it.copy(loaded = true)
        }
        if(mutableStateFlow.value.loaded){
            currentJob = viewModelScope.launch{
                callAPI()
            }
        }
    }

    fun downloadApkFromLink(link: String){
        currentJob?.cancel()
        currentJob = viewModelScope.launch{
            DownloadUtil.downloadSpotify(link, Spowlo.context)
        }
    }

   private fun callAPI(){
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            getApiResponse()
                .onEach { result ->
                when(result){
                    is Resource.Success -> {
                        _state.value = state.value.copy(
                            APIResponse = result.data ?: APIResponse(
                                Regular_Latest = "",
                                Amoled_Latest = "",
                                RC_Latest = "",
                                ABC_Latest = "",
                                Regular = emptyList(),
                                Amoled = emptyList(),
                                Regular_Cloned = emptyList(),
                                Amoled_Cloned = emptyList()
                            ),
                            isLoading = false
                        )
                        println(state.value.APIResponse)

                        val localAPIResponse = state.value.APIResponse

                        mutableStateFlow.update {
                            it.copy(
                                regular_versions = localAPIResponse.Regular,
                                regular_cloned_versions = localAPIResponse.Regular_Cloned,
                                amoled_versions = localAPIResponse.Amoled,
                                amoled_cloned_versions = localAPIResponse.Amoled_Cloned,
                                regular_latest_version = localAPIResponse.Regular_Latest,
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.value = state.value.copy(
                            APIResponse = result.data ?: APIResponse(
                                Regular_Latest = "",
                                Amoled_Latest = "",
                                RC_Latest = "",
                                ABC_Latest = "",
                                Regular = emptyList(),
                                Amoled = emptyList(),
                                Regular_Cloned = emptyList(),
                                Amoled_Cloned = emptyList()
                            ),
                            isLoading = false
                        )
                    }
                    is Resource.Loading -> {
                        _state.value = state.value.copy(
                            APIResponse = result.data ?: APIResponse(
                                Regular_Latest = "",
                                Amoled_Latest = "",
                                RC_Latest = "",
                                ABC_Latest = "",
                                Regular = emptyList(),
                                Amoled = emptyList(),
                                Regular_Cloned = emptyList(),
                                Amoled_Cloned = emptyList()
                            ),
                            isLoading = true
                        )
                    }
                }
            }.launchIn(this)
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
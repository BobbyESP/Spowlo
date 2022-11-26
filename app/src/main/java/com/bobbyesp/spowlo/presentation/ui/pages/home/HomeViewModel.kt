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
    private val emptyAPIList: APIResponse = APIResponse("", "", "", "", "", emptyList(), emptyList(), emptyList(), emptyList(), emptyList())

    data class HomeViewState(
        val regular_versions: List<PackagesObject> = mutableListOf(),
        val regular_cloned_versions: List<PackagesObject> = mutableListOf(),
        val amoled_versions: List<PackagesObject> = mutableListOf(),
        val amoled_cloned_versions: List<PackagesObject> = mutableListOf(),
        val liteVersions: List<PackagesObject> = mutableListOf(),
        val cpuArch: String = "",
        val originalSpotifyVersion: String = "",
        val regularSpotifyVersion: String = "",
        val clonedSpotifyVersion: String = "",
        val amoledSpotifyVersion: String = "",
        val amoledClonedSpotifyVersion: String = "",
        val liteSpotifyVersion: String = "",
        val loaded: Boolean = false,
        val regular_latest_version: String = "",
        val isError: Boolean = false,
        val isLoading: Boolean = true,
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
            DownloadUtil.openLinkInBrowser(link)
        }
    }

    fun sortPackagesByVersion(list: List<PackagesObject>): List<PackagesObject>{
        return list.sortedByDescending { it.Title }
    }

   private fun callAPI(){
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            getApiResponse()
                .onEach { result ->
                when(result){
                    is Resource.Success -> {
                        _state.value = state.value.copy(
                            APIResponse = result.data ?: emptyAPIList,
                            isLoading = false
                        )
                        val localAPIResponse = state.value.APIResponse

                        mutableStateFlow.update {
                            it.copy(
                                regular_versions = localAPIResponse.Regular,
                                regular_cloned_versions = localAPIResponse.Regular_Cloned,
                                amoled_versions = localAPIResponse.Amoled,
                                amoled_cloned_versions = localAPIResponse.Amoled_Cloned,
                                regular_latest_version = localAPIResponse.Regular_Latest,
                                liteSpotifyVersion = localAPIResponse.Lite_Latest,
                                liteVersions = localAPIResponse.Lite,
                                regularSpotifyVersion = localAPIResponse.Regular_Latest,
                                amoledSpotifyVersion = localAPIResponse.Amoled_Latest,
                                amoledClonedSpotifyVersion = localAPIResponse.ABC_Latest,
                                clonedSpotifyVersion = localAPIResponse.RC_Latest,
                                //UI stuff
                                isError = false,
                                isLoading = false,
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.value = state.value.copy(
                            APIResponse = result.data ?: emptyAPIList,
                            isLoading = false
                        )
                        mutableStateFlow.update {
                            it.copy(
                                isError = true,
                                isLoading = false,
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _state.value = state.value.copy(
                            APIResponse = result.data ?: emptyAPIList,
                            isLoading = true
                        )
                        mutableStateFlow.update {
                            it.copy(
                                isLoading = true,
                            )
                        }
                    }
                }
            }.launchIn(this)
        }
    }

    private fun sortPackagesObjectList(list: List<PackagesObject>): List<PackagesObject>{
        return list.sortedWith(compareByDescending { it.Title })
    }

    //TODO: Move this to a util class and take a look to it because it's listing reversed
    private fun sortAllPackages(){
        mutableStateFlow.update {
            it.copy(
                regular_versions = sortPackagesObjectList(it.regular_versions),
                regular_cloned_versions = sortPackagesObjectList(it.regular_cloned_versions),
                amoled_versions = sortPackagesObjectList(it.amoled_versions),
                amoled_cloned_versions = sortPackagesObjectList(it.amoled_cloned_versions),
            )
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
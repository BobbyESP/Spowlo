package com.bobbyesp.spowlo.presentation.ui.pages.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobbyesp.spowlo.data.remote.APIHelper
import com.bobbyesp.spowlo.data.remote.ApiHelperImpl
import com.bobbyesp.spowlo.domain.spotify.model.APIResponse
import com.bobbyesp.spowlo.domain.spotify.model.PackagesObject
import com.bobbyesp.spowlo.util.CPUInfoUtil
import com.bobbyesp.spowlo.util.VersionsUtil
import com.bobbyesp.spowlo.util.api.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiHelper: APIHelper
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
        val loaded: Boolean = false
        )

    private val apiResponse = MutableLiveData<Resource<APIResponse>>()

    fun setup(){
        //execute infoCard function, and when it finished, update the loaded value to boolean true
        currentJob?.cancel()
        currentJob = viewModelScope.launch(Dispatchers.IO){
            infoCard()
        }
        mutableStateFlow.update {
            it.copy(loaded = true)
        }
        callAPI()
        getAPIResponse()
        //call apiResponseToPackagesList function and pass parameters
        apiResponse.value?.data?.let { apiResponseToPackagesList(it) }


    }

    private fun callAPI(){
        //fetch the packages from the API
        viewModelScope.launch {
            apiResponse.postValue(Resource.loading(null))
            try {
                val response = apiHelper.getAPIInfo()
                apiResponse.postValue(Resource.success(response))
            } catch (e: Exception) {
                apiResponse.postValue(Resource.error("An error occurred", null))
            }
        }
    }

    private fun getAPIResponse(): MutableLiveData<Resource<APIResponse>> {
        return apiResponse
    }

    private fun apiResponseToPackagesList(apiResponse: APIResponse): List<PackagesObject> {
        //convert the API response to a list of packages
        val packagesList = mutableListOf<PackagesObject>()
        apiResponse.Regular.forEach { (key, value) ->
            packagesList.add(PackagesObject(key, value))
        }
        //pass the package list to the regular_versions list
        mutableStateFlow.update {
            it.copy(regular_versions = packagesList)
        }

        //create other list for amoled
        val amoledPackagesList = mutableListOf<PackagesObject>()
        apiResponse.Amoled.forEach { (key, value) ->
            amoledPackagesList.add(PackagesObject(key, value))
        }
        //pass the package list to the amoled_versions list
        mutableStateFlow.update {
            it.copy(amoled_versions = amoledPackagesList)
        }

        //create other list for regular cloned
        val regularClonedPackagesList = mutableListOf<PackagesObject>()
        apiResponse.Regular_Cloned.forEach { (key, value) ->
            regularClonedPackagesList.add(PackagesObject(key, value))
        }
        //pass the package list to the regular_cloned_versions list
        mutableStateFlow.update {
            it.copy(regular_cloned_versions = regularClonedPackagesList)
        }

        //create other list for amoled cloned
        val amoledClonedPackagesList = mutableListOf<PackagesObject>()
        apiResponse.Amoled_Cloned.forEach { (key, value) ->
            amoledClonedPackagesList.add(PackagesObject(key, value))
        }

        //pass the package list to the amoled_cloned_versions list
        mutableStateFlow.update {
            it.copy(amoled_cloned_versions = amoledClonedPackagesList)
        }
        return packagesList
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
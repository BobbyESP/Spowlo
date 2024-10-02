package com.bobbyesp.spowlo.ui.pages.mod_downloader

import androidx.compose.material.ExperimentalMaterialApi
import androidx.lifecycle.ViewModel
import com.bobbyesp.spowlo.features.mod_downloader.domain.model.APIResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
class ModsDownloaderViewModel @Inject constructor() : ViewModel() {

    val apiResponseFlow = MutableStateFlow(APIResponseDto())
    fun updateApiResponse(apiResponseDto: APIResponseDto) {
        apiResponseFlow.update {
            apiResponseDto
        }
    }

}
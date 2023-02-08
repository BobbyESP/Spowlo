package com.bobbyesp.spowlo.ui.pages.mod_downloader

import androidx.compose.material.ExperimentalMaterialApi
import androidx.lifecycle.ViewModel
import com.bobbyesp.spowlo.features.mod_downloader.domain.model.PackagesResponseDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class ModsDownloaderViewModel @Inject constructor(): ViewModel() {

    val apiResponseFlow = MutableStateFlow(PackagesResponseDto())
    fun updateApiResponse (packagesResponseDto: PackagesResponseDto) {
        apiResponseFlow.update {
            packagesResponseDto
        }
    }

}
package com.bobbyesp.spowlo.presentation.ui.pages.downloader_page

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(
    ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class
)
@Composable
fun DownloaderPage(
    navController: NavController,
    downloadViewModel: DownloaderViewModel = hiltViewModel(),
) {

}
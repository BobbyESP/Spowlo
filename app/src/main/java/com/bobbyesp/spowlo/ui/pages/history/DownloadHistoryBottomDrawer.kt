package com.bobbyesp.spowlo.ui.pages.history

import androidx.activity.compose.BackHandler
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun DownloadHistoryBottomDrawer(downloadsHistoryViewModel: DownloadsHistoryViewModel = hiltViewModel()) {

    val viewState = downloadsHistoryViewModel.detailViewState.collectAsStateWithLifecycle().value
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    BackHandler(viewState.drawerState.targetValue == ModalBottomSheetValue.Expanded) {
        downloadsHistoryViewModel.hideDrawer(scope)
    }


}
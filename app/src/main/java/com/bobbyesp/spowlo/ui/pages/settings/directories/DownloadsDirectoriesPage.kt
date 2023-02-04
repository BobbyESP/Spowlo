package com.bobbyesp.spowlo.ui.pages.settings.directories

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import com.bobbyesp.spowlo.utils.CUSTOM_PATH
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.SUBDIRECTORY
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsDirectoriesPage(
    onBackPressed: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var isSubdirectoryEnabled
            by remember { mutableStateOf(PreferencesUtil.getValue(SUBDIRECTORY)) }
    var isCustomPathEnabled
            by remember { mutableStateOf(PreferencesUtil.getValue(CUSTOM_PATH)) }


}
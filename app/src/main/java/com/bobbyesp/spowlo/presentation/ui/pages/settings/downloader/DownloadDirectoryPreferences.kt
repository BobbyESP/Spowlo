package com.bobbyesp.spowlo.presentation.ui.pages.settings.downloader

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.StorageId
import com.bobbyesp.spowlo.Spowlo
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.Spowlo.Companion.context
import com.bobbyesp.spowlo.presentation.MainActivity
import com.bobbyesp.spowlo.presentation.ui.components.BackButton
import com.bobbyesp.spowlo.util.FileUtil
import com.bobbyesp.spowlo.util.PreferencesUtil
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch

const val ytdlpOutputTemplateReference = "https://github.com/yt-dlp/yt-dlp#output-template"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DownloadDirectoryPreferences(onBackPressed: () -> Unit) {

    val uriHandler = LocalUriHandler.current
    val storageIds = DocumentFileCompat.getStorageIds(context)
    var grantedUris: List<String> = mutableListOf()
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    val context = LocalContext.current
   // var videoDirectoryText by remember { mutableStateOf(Spowlo.videoDownloadDir) }
   // var audioDirectoryText by remember { mutableStateOf(Spowlo.audioDownloadDir) }
    var pathTemplateText by remember { mutableStateOf(PreferencesUtil.getOutputPathTemplate()) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(id = R.string.download_directory),
                    )
                },
                navigationIcon = {
                    BackButton(modifier = Modifier.padding(start = 8.dp), onClick = onBackPressed)
                }, scrollBehavior = scrollBehavior
            )
        }, content = {
            LazyColumn(modifier = Modifier.padding(it)) {
                item {
                    storageIds.forEach { it ->
                        val storageId = storageIds[storageIds.indexOf(it)]
                        val storageName = if (storageId == StorageId.PRIMARY) "External Storage" else storageId

                        Text(text = storageName, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(8.dp))
                    }
                }
                item{
                    Text(text = "Video Download Directory", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(8.dp))
                    grantedUris.forEach {
                        //Create a text with the uri and a button to copy it to the clipboard
                        Text(text = it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    )
    //Launched effect to show granted uris
    LaunchedEffect(Unit) {
        launch {
            FileUtil.getListOfGrantedUris().let {
                grantedUris = it
            }
        }
    }
}
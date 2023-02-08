package com.bobbyesp.spowlo.ui.pages.mod_downloader

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.mod_downloader.ui.components.PackagesListItem
import com.bobbyesp.spowlo.features.mod_downloader.ui.components.PackagesListItemType
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.pages.downloader.DownloaderViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun ModsDownloaderPage(
    onBackPressed: () -> Unit,
    modsDownloaderViewModel: ModsDownloaderViewModel
) {
    val apiResponse = modsDownloaderViewModel.apiResponseFlow.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(title = {
                Text(
                    text = stringResource(id = R.string.mods_downloader),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                )
            }, navigationIcon = {
                BackButton { onBackPressed() }
            }, actions = {
            }, scrollBehavior = scrollBehavior
            )
        }) { paddings ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddings).padding(16.dp)) {
            with(apiResponse.value){
                item{
                    PackagesListItem(
                        type = PackagesListItemType.Regular,
                        expanded = false,
                        onClick = {},
                        packages = Regular, //regularVersions.sortedByDescending { it.Title },
                        latestVersion = Regular_Latest
                    )
                }
                item {
                    PackagesListItem(
                        type = PackagesListItemType.RegularCloned,
                        expanded = false,
                        onClick = {},
                        packages = Regular_Cloned, //betaVersions.sortedByDescending { it.Title },
                        latestVersion = Regular_Latest
                    )
                }
                item {
                    PackagesListItem(
                        type = PackagesListItemType.Amoled,
                        expanded = false,
                        onClick = {},
                        packages = Amoled, //betaVersions.sortedByDescending { it.Title },
                        latestVersion = Amoled_Latest
                    )
                }
                item {
                    PackagesListItem(
                        type = PackagesListItemType.AmoledCloned,
                        expanded = false,
                        onClick = {},
                        packages = Amoled_Cloned, //betaVersions.sortedByDescending { it.Title },
                        latestVersion = Amoled_Latest
                    )
                }

                item {
                    PackagesListItem(
                        type = PackagesListItemType.Lite,
                        expanded = false,
                        onClick = {},
                        packages = Lite, //betaVersions.sortedByDescending { it.Title },
                        latestVersion = Lite_Latest
                    )
                }

            }
        }
    }
}
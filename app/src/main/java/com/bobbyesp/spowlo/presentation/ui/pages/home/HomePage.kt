package com.bobbyesp.spowlo.presentation.ui.pages.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bobbyesp.spowlo.presentation.ui.common.Route
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.presentation.ui.components.ArchType
import com.bobbyesp.spowlo.presentation.ui.components.PackagesListItem
import com.bobbyesp.spowlo.presentation.ui.components.PackagesListItemType
import com.bobbyesp.spowlo.presentation.ui.components.RelevantInfoItem
import com.bobbyesp.spowlo.util.CPUInfoUtil
import com.bobbyesp.spowlo.util.VersionsUtil

@OptIn(
    ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class
)
@Composable
fun HomePage(navController: NavController, homeViewModel: HomeViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val hapticFeedback = LocalHapticFeedback.current
    // val keyboardController = LocalSoftwareKeyboardController.current
    val viewState = homeViewModel.stateFlow.collectAsState()

    val regularVersions = viewState.value.regular_versions
    val regularClonedVersions = viewState.value.regular_cloned_versions
    val amoledVersions = viewState.value.amoled_versions
    val amoledClonedVersions = viewState.value.amoled_cloned_versions
    val liteVersions = viewState.value.liteVersions
    //Version strings
    val regularLastVersion = viewState.value.regularSpotifyVersion
    val regularClonedLastVersion = viewState.value.clonedSpotifyVersion
    val amoledLastVersion = viewState.value.amoledSpotifyVersion
    val amoledClonedLastVersion = viewState.value.amoledClonedSpotifyVersion
    val liteLastVersion = viewState.value.liteSpotifyVersion

    with(viewState.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                topBar = {
                    TopAppBar(
                        title = {
                        },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        navigationIcon = {
                            IconButton(onClick = {
                                navController.navigate(Route.SETTINGS)
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = stringResource(id = R.string.settings)
                                )
                            }
                        })
                }) {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                )
                {
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(48.dp)
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        )
                        {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Start)
                                    .padding(start = 8.dp, top = 12.dp, bottom = 12.dp, end = 8.dp)
                            )
                            {
                                Text(
                                    modifier = Modifier,
                                    text = stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.displaySmall
                                )
                            }
                            PackagesListItem(
                                type = PackagesListItemType.Regular,
                                expanded = false,
                                onClick = {},
                                packages = homeViewModel.sortPackagesByVersion(regularVersions), //regularVersions.sortedByDescending { it.Title },
                                latestVersion = regularLastVersion
                            )
                            PackagesListItem(
                                type = PackagesListItemType.RegularCloned,
                                expanded = false,
                                onClick = {},
                                packages = homeViewModel.sortPackagesByVersion(regularClonedVersions),
                                latestVersion = regularClonedLastVersion
                            )
                            PackagesListItem(
                                type = PackagesListItemType.Amoled,
                                expanded = false,
                                onClick = {},
                                packages = homeViewModel.sortPackagesByVersion(amoledVersions),
                                latestVersion = amoledLastVersion
                            )
                            PackagesListItem(
                                type = PackagesListItemType.AmoledCloned,
                                expanded = false,
                                onClick = {},
                                packages = homeViewModel.sortPackagesByVersion(amoledClonedVersions),
                                latestVersion = amoledClonedLastVersion
                            )
                            PackagesListItem(
                                type = PackagesListItemType.Lite,
                                expanded = false,
                                onClick = {},
                                packages = homeViewModel.sortPackagesByVersion(liteVersions),
                                latestVersion = liteLastVersion
                            )
                            Divider(modifier = Modifier.padding(top = 16.dp, bottom = 14.dp))
                            AnimatedVisibility(visible = loaded) {
                                when (loaded) {
                                    false -> {
                                        Box(modifier = Modifier.fillMaxWidth()) {
                                            CircularProgressIndicator(
                                                modifier = Modifier
                                                    .align(Alignment.Center)
                                                    .size(24.dp)
                                            )
                                        }
                                    }
                                    true -> {
                                        RelevantInfoItem(
                                            cpuArch = cpuArch,
                                            originalSpotifyVersion = originalSpotifyVersion,
                                            clonedSpotifyVersion = clonedSpotifyVersion,
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

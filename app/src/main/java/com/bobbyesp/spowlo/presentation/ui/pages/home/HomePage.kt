package com.bobbyesp.spowlo.presentation.ui.pages.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bobbyesp.spowlo.presentation.ui.common.Route
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.bobbyesp.spowlo.R
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
fun HomePage(navController: NavController) {
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val hapticFeedback = LocalHapticFeedback.current
    val keyboardController = LocalSoftwareKeyboardController.current

    //save expanded states for each item
    val expandedStates = remember { mutableMapOf<Int, Boolean>() }

    //get expanded states for each item
    fun getExpandedState(type: PackagesListItemType): Boolean {
        return expandedStates[type.type] ?: false
    }

    //save expanded states for each item
    fun setExpandedState(type: PackagesListItemType, expanded: Boolean) {
        expandedStates[type.type] = expanded
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        Scaffold(modifier = Modifier
            .align(Alignment.Center)
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
            modifier = Modifier.padding(horizontal = 8.dp),
            navigationIcon = {
                IconButton(onClick = {navController.navigate(Route.SETTINGS) }) {
                    Icon(
                       imageVector = Icons.Outlined.Settings,
                       contentDescription = stringResource(id = R.string.settings)
                    )
                }
            })
        }) {
            Column(modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()))
            {
                Column(modifier = Modifier
                    .padding(16.dp))
                {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Start)
                        .padding(start = 8.dp, top = 12.dp, bottom = 12.dp))
                    {
                        Text(
                            modifier = Modifier,
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.displaySmall
                        )
                    }
                    PackagesListItem( type = PackagesListItemType.Regular, expanded = getExpandedState(PackagesListItemType.Regular), onClick = { setExpandedState(PackagesListItemType.Regular, !getExpandedState(PackagesListItemType.Regular)) })
                    PackagesListItem( type = PackagesListItemType.RegularCloned, expanded = getExpandedState(PackagesListItemType.RegularCloned), onClick = { setExpandedState(PackagesListItemType.RegularCloned, !getExpandedState(PackagesListItemType.RegularCloned)) })
                    PackagesListItem( type = PackagesListItemType.Amoled, expanded = getExpandedState(PackagesListItemType.Amoled), onClick = { setExpandedState(PackagesListItemType.Amoled, !getExpandedState(PackagesListItemType.Amoled)) })
                    PackagesListItem( type = PackagesListItemType.AmoledCloned, expanded = getExpandedState(PackagesListItemType.AmoledCloned), onClick = { setExpandedState(PackagesListItemType.AmoledCloned, !getExpandedState(PackagesListItemType.AmoledCloned)) })
                    Divider(modifier = Modifier.padding(top = 16.dp, bottom = 14.dp))
                    RelevantInfoItem(
                        cpuArch = CPUInfoUtil.getPrincipalCPUArch(),
                        originalSpotifyVersion = VersionsUtil.getSpotifyVersion(type = "regular"),
                        clonedSpotifyVersion = VersionsUtil.getSpotifyVersion(type = "cloned"),
                    )
                }

            }
        }
    }

}

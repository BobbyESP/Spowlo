package com.bobbyesp.spowlo.presentation.ui.pages.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DisplaySettings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bobbyesp.spowlo.presentation.ui.components.BackButton
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.presentation.ui.common.Route
import com.bobbyesp.spowlo.presentation.ui.components.SettingItem
import com.bobbyesp.spowlo.util.CPUInfoUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(navController: NavController) {
    val context = LocalContext.current
    val cpuArch = CPUInfoUtil.getCPUArch()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier) {
            TopAppBar(title= {},
            modifier = Modifier.padding(start = 8.dp),
            navigationIcon = { BackButton { navController.popBackStack() } })
            Text(
                modifier = Modifier.padding(start = 24.dp, top = 48.dp),
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineLarge
            )
            LazyColumn(modifier = Modifier
                .padding(top = 24.dp)) {
                item{
                    SettingItem(
                        title = stringResource(R.string.general),
                        description = stringResource(id = R.string.general_description),
                        icon = Icons.Outlined.Settings,
                    ){
                        navController.navigate(Route.GENERAL_SETTINGS){
                            launchSingleTop = true
                        }
                    }
                }
                item { 
                    SettingItem(title = stringResource(id = R.string.display), description = stringResource(
                        id = R.string.display_description),
                        icon = Icons.Outlined.DisplaySettings) {
                        navController.navigate(Route.DISPLAY_SETTINGS){
                            launchSingleTop = true
                        }
                    }
                }
                item {
                    SettingItem(title = stringResource(id = R.string.about), description = stringResource(
                        id = R.string.about_description),
                        icon = Icons.Outlined.Settings) {
                        navController.navigate(Route.ABOUT_SETTINGS){
                            launchSingleTop = true
                        }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize()){
                Text(text = "CPU Arch: $cpuArch",
                    modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}
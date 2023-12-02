package com.bobbyesp.spowlo.ui.pages.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.topbars.SmallTopAppBar
import com.bobbyesp.spowlo.utils.files.FilesUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    viewModel: HomePageViewModel,
    isLogged: Boolean,
    onLoginRequest: () -> Unit
) {
    val navController = LocalNavController.current
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate(Route.SettingsNavigator.route)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings Icon",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Route.Notifications.route) }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications Icon"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLogged) {
                Text(text = "Logged in")
            } else {
                Text(text = "Not logged in")
                Button(
                    onClick = {
                        onLoginRequest()
                    }) {
                    Text(text = "Launch PKCE Auth flow")
                }
            }
            Button(onClick = {
                viewModel.deleteEncryptedSharedPrefs()

                viewModel.viewModelScope.launch {
                    viewModel.getLoggedIn()
                }
            }) {
                Text(text = "Delete auth data")
            }

            Button(
                onClick = {
                    FilesUtil.SharedPreferences.deleteSharedPreferences(context = context)
                }) {
                Text(text = "Force delete credentials")
            }
            Button(onClick = { error("Test") }) {
                Text(text = "Test crash")
            }
            Button(onClick = { error("contained an invalid tag (zero)") }) {
                Text(text = "Test crash 2")
            }
        }
    }
}
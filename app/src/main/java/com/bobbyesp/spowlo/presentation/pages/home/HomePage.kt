package com.bobbyesp.spowlo.presentation.pages.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bobbyesp.spowlo.features.notification_manager.domain.model.Notification
import com.bobbyesp.spowlo.presentation.common.LocalNavController
import com.bobbyesp.spowlo.presentation.common.LocalNotificationManager
import com.bobbyesp.spowlo.presentation.common.LocalSnackbarHostState
import com.bobbyesp.spowlo.presentation.common.Route
import kotlinx.coroutines.launch

@Composable
fun HomePage() {
    val navController = LocalNavController.current
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Hello, Spotify!")
        Button(onClick = {
            scope.launch {
                snackbarHostState.showSnackbar("Hello, Snackbar!")
            }
        }) {
            Text("Show Snackbar")
        }
        val notificationsManager = LocalNotificationManager.current
        Button(onClick = {
            scope.launch {
                notificationsManager.showNotification(
                    Notification.default().copy(
                        title = "Hello, Notification!",
                        subtitle = "This is a notification!"
                    )
                )
            }
        }) {
            Text("Show notification")
        }

        Button(onClick = {
            navController.navigate(Route.Spotify.Auth)
        }) {
            Text("Open auth screen")
        }
    }
}
package com.bobbyesp.spowlo.features.notification_manager.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.presentation.common.LocalNotificationManager
import com.bobbyesp.utilities.ui.applyAlpha
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NotificationsHandler(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    val notificationManager = LocalNotificationManager.current
    val notificationState by notificationManager.getCurrentNotification()
        .collectAsStateWithLifecycle()

    val notificationVisible = notificationState != null

    if (notificationVisible) {
        DisposableEffect(notificationState) {
            val job = scope.launch {
                delay(notificationState!!.duration.time)
                notificationManager.dismissNotification()
            }

            onDispose {
                job.cancel()
            }
        }
    }

    AnimatedVisibility(
        visible = notificationVisible,
        enter = fadeIn(), // You can customize enter and exit animations
        exit = fadeOut() // As an example, fadeIn and fadeOut are used
    ) {
        Box(
            modifier = modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.applyAlpha(0.6f), Color.Transparent
                        ), endY = 500f
                    )
                )
                .fillMaxSize(), contentAlignment = Alignment.TopCenter
        ) {
            notificationState?.let { notification ->
                var contentVisible by remember { mutableStateOf(false) }
                if (!contentVisible) {
                    scope.launch {
                        delay(300) // Introduce a delay of 300ms
                        contentVisible = true
                    }
                }

                // Use transition to animate the card's appearance
                val transition = updateTransition(
                    targetState = contentVisible, label = "Content visibility transition"
                )
                val offset by transition.animateDp(
                    transitionSpec = { tween(durationMillis = 500) },
                    label = "Content offset transition",
                ) { isVisible ->
                    if (isVisible) 40.dp else (-100).dp
                }

                Card(
                    modifier = Modifier.offset(y = offset),
                ) {
                    Text(
                        text = notification.title,
                        modifier = Modifier,
                        color = Color.White
                    )
                }
            }
        }
    }
}
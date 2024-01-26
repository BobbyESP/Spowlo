package com.bobbyesp.spowlo.features.inapp_notifications.domain.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import kotlin.random.Random

@Stable
data class Notification(
    val id: Int = Random.nextInt(),
    val title: String,
    val subtitle: String,
    val timestamp: Long = System.currentTimeMillis(),
    val entityInfo : SpEntityNotificationInfo? = null,
    val content: @Composable (() -> Unit)? = null,
)

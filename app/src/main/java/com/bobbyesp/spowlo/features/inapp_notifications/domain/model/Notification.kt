package com.bobbyesp.spowlo.features.inapp_notifications.domain.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import kotlin.random.Random

enum class DURATION(val duration: Long) {
    SHORT(3000),
    MEDIUM(4000),
    LONG(8000),
}

@Stable
data class Notification(
    val id: Int = Random.nextInt(),
    val title: String,
    val subtitle: String,
    val duration: DURATION = DURATION.MEDIUM,
    val timestamp: Long = System.currentTimeMillis(),
    val entityInfo: SpEntityNotificationInfo? = null,
    val content: @Composable (() -> Unit)? = null,
)

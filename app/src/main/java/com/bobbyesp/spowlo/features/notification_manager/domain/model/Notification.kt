package com.bobbyesp.spowlo.features.notification_manager.domain.model

import androidx.compose.runtime.Composable
import kotlin.random.Random

data class Notification(
    val id: Int = Random.nextInt(),
    val title: String,
    val subtitle: String,
    val timestamp: Long = System.currentTimeMillis(),
    val content: @Composable (() -> Unit)? = null,
)

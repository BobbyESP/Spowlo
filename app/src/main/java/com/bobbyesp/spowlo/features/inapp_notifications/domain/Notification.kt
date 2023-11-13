package com.bobbyesp.spowlo.features.inapp_notifications.domain

import androidx.compose.runtime.Composable

data class Notification(
    val id: Int,
    val title: String,
    val subtitle: String,
    val timestamp: Long = System.currentTimeMillis(),
    val content: @Composable () -> Unit,
)

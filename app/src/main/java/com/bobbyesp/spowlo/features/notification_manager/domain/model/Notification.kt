package com.bobbyesp.spowlo.features.notification_manager.domain.model

import androidx.compose.runtime.Stable
import kotlin.random.Random

enum class DURATION(val time: Long) {
    SHORT(3000),
    MEDIUM(4000),
    LONG(8000),
    INFINITE(Long.MAX_VALUE),
}

@Stable
data class Notification(
    val id: Int,
    val title: String,
    val subtitle: String,
    val duration: DURATION,
    val timestamp: Long,
) {
    companion object {
        fun default() = Notification(
            id = Random.nextInt(),
            title = "",
            subtitle = "",
            duration = DURATION.MEDIUM,
            timestamp = System.currentTimeMillis()
        )
    }
}